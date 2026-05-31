package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vbc.dto.VideoUploadDTO;
import com.vbc.entity.Frame;
import com.vbc.entity.Video;
import com.vbc.repository.FrameMapper;
import com.vbc.repository.VideoMapper;
import com.vbc.service.VideoService;
import com.vbc.util.FileUtil;
import com.vbc.util.VideoConverter;
import com.vbc.vo.FrameVO;
import com.vbc.vo.PageResult;
import com.vbc.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;
    private final FrameMapper frameMapper;

    @Value("${vbc.upload.video-path}")
    private String videoUploadPath;

    private Path getUploadDir() {
        Path path = Paths.get(videoUploadPath);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(videoUploadPath);
        }
        return path.toAbsolutePath().normalize();
    }

    @Override
    public PageResult<VideoVO> listVideos(Integer page, Integer pageSize, String keyword, String status, Long deviceId) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Video::getTitle, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Video::getStatus, status);
        }
        if (deviceId != null) {
            wrapper.eq(Video::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(Video::getCreatedAt);

        Page<Video> result = videoMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<VideoVO> records = result.getRecords().stream().map(v -> {
            VideoVO vo = new VideoVO();
            BeanUtils.copyProperties(v, vo);
            if (StringUtils.hasText(v.getThumbnailPath())) {
                vo.setThumbnailUrl("/api/v1/files/thumbnails/" + new File(v.getThumbnailPath()).getName());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }

    @Override
    public VideoVO getVideo(Long id) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            throw new IllegalArgumentException("视频不存在");
        }
        VideoVO vo = new VideoVO();
        BeanUtils.copyProperties(video, vo);
        if (StringUtils.hasText(video.getThumbnailPath())) {
            vo.setThumbnailUrl("/api/v1/files/thumbnails/" + new File(video.getThumbnailPath()).getName());
        }
        return vo;
    }

    @Override
    public VideoVO uploadVideo(MultipartFile file, VideoUploadDTO dto) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".mp4";
        String storedFilename = UUID.randomUUID().toString() + extension;

        try {
            Path uploadDir = getUploadDir();
            Files.createDirectories(uploadDir);
            Path targetPath = uploadDir.resolve(storedFilename);
            file.transferTo(targetPath.toFile());
            log.info("Video saved to: {}", targetPath);

            // Auto-convert to H.264 for browser compatibility
            try {
                String convertedFilename = storedFilename.replace(extension, "_h264" + extension);
                Path convertedPath = uploadDir.resolve(convertedFilename);
                VideoConverter.convertToH264(targetPath.toString(), convertedPath.toString());
                targetPath = convertedPath;
                storedFilename = convertedFilename;
                log.info("Video converted to H.264: {}", targetPath);
            } catch (Exception e) {
                log.warn("H.264 conversion failed, keeping original: {}", e.getMessage());
            }

            Video video = new Video();
            video.setTitle(dto.getTitle() != null ? dto.getTitle() : "Untitled");
            video.setFileName(originalFilename != null ? originalFilename : "unknown.mp4");
            video.setFilePath(targetPath.toString().replace("\\", "/"));
            video.setFileSize(file.getSize());
            video.setStatus("READY");
            video.setDeviceId(dto.getDeviceId());
            videoMapper.insert(video);

            VideoVO vo = new VideoVO();
            BeanUtils.copyProperties(video, vo);
            return vo;
        } catch (IOException e) {
            log.error("Failed to save video file", e);
            throw new RuntimeException("视频文件保存失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteVideo(Long id) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            throw new IllegalArgumentException("视频不存在");
        }
        // Delete physical file
        try {
            Files.deleteIfExists(Paths.get(video.getFilePath()));
            if (StringUtils.hasText(video.getThumbnailPath())) {
                Files.deleteIfExists(Paths.get(video.getThumbnailPath()));
            }
        } catch (IOException e) {
            log.warn("Failed to delete physical files for video {}", id, e);
        }
        videoMapper.deleteById(id);
    }

    @Override
    public List<FrameVO> listFrames(Long videoId, Integer startIndex, Integer endIndex) {
        LambdaQueryWrapper<Frame> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Frame::getVideoId, videoId)
                .ge(Frame::getFrameIndex, startIndex)
                .le(Frame::getFrameIndex, endIndex)
                .orderByAsc(Frame::getFrameIndex);
        List<Frame> frames = frameMapper.selectList(wrapper);
        return frames.stream().map(f -> {
            String filename = FileUtil.getFileName(f.getFilePath());
            return new FrameVO(f.getId(), f.getVideoId(), f.getFrameIndex(),
                    f.getTimestampSeconds(), "/api/v1/files/frames/" + filename, f.getProcessed());
        }).toList();
    }
}
