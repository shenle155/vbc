package com.vbc.service;

import com.vbc.dto.VideoUploadDTO;
import com.vbc.vo.FrameVO;
import com.vbc.vo.PageResult;
import com.vbc.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    PageResult<VideoVO> listVideos(Integer page, Integer pageSize, String keyword, String status, Long deviceId);
    VideoVO getVideo(Long id);
    VideoVO uploadVideo(MultipartFile file, VideoUploadDTO dto);
    void deleteVideo(Long id);
    List<FrameVO> listFrames(Long videoId, Integer startIndex, Integer endIndex);
}
