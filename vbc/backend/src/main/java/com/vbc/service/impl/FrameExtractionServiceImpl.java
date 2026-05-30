package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vbc.dto.FrameExtractDTO;
import com.vbc.entity.Frame;
import com.vbc.entity.Video;
import com.vbc.repository.FrameMapper;
import com.vbc.repository.VideoMapper;
import com.vbc.service.FrameExtractionService;
import com.vbc.service.NotificationService;
import com.vbc.util.FileUtil;
import com.vbc.util.VideoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrameExtractionServiceImpl implements FrameExtractionService {

    private final VideoMapper videoMapper;
    private final FrameMapper frameMapper;
    private final NotificationService notificationService;

    @Value("${vbc.upload.frame-path}")
    private String framePath;

    @Override
    @Async
    public void extractFrames(Long videoId, FrameExtractDTO dto) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            log.warn("Video not found: {}", videoId);
            return;
        }

        // Update status to PROCESSING
        video.setStatus("PROCESSING");
        videoMapper.updateById(video);
        notifyStatus(videoId, "PROCESSING", "开始提取帧...");

        // Extract metadata first
        VideoUtil.VideoMeta meta = VideoUtil.extractMeta(video.getFilePath());
        video.setDurationSeconds(meta.durationSeconds);
        video.setFps(meta.fps);
        video.setWidth(meta.width);
        video.setHeight(meta.height);

        // Generate thumbnail from first frame
        String thumbDir = framePath.replace("frames", "thumbnails");
        String thumbFilename = UUID.randomUUID().toString() + ".jpg";
        String thumbPath = thumbDir + "/" + thumbFilename;
        try {
            VideoUtil.grabFrame(video.getFilePath(), 0, thumbPath);
            video.setThumbnailPath(thumbPath);
        } catch (Exception e) {
            log.warn("Failed to generate thumbnail for video {}", videoId, e);
        }
        videoMapper.updateById(video);

        // Extract frames at interval
        double interval = dto.getIntervalSeconds() != null ? dto.getIntervalSeconds() : 1.0;
        int maxFrames = dto.getMaxFrames() != null ? dto.getMaxFrames() : 300;
        int totalFrames = Math.min((int) (meta.durationSeconds / interval), maxFrames);

        int successCount = 0;
        for (int i = 0; i < totalFrames; i++) {
            double second = i * interval;
            String frameFilename = videoId + "_" + String.format("%05d", i) + ".jpg";
            String outputPath = framePath + "/" + frameFilename;

            try {
                String result = VideoUtil.grabFrame(video.getFilePath(), second, outputPath);
                if (result != null) {
                    Frame frame = new Frame();
                    frame.setVideoId(videoId);
                    frame.setFrameIndex(i);
                    frame.setTimestampSeconds(second);
                    frame.setFilePath(result);
                    frame.setProcessed(false);
                    frameMapper.insert(frame);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to extract frame {} of video {}", i, videoId, e);
            }
        }

        // Mark video as READY
        video.setStatus("READY");
        videoMapper.updateById(video);

        log.info("Frame extraction complete for video {}: {}/{} frames extracted", videoId, successCount, totalFrames);
        notifyStatus(videoId, "READY", "帧提取完成，共 " + successCount + " 帧");
    }

    private void notifyStatus(Long videoId, String status, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "STATUS_CHANGE");
        payload.put("videoId", videoId);
        payload.put("status", status);
        payload.put("message", message);
        payload.put("timestamp", Instant.now().toEpochMilli());
        notificationService.broadcast("/topic/video/" + videoId + "/status", payload);
    }
}
