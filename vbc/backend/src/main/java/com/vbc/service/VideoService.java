package com.vbc.service;

import com.vbc.dto.VideoUploadDTO;
import com.vbc.vo.PageResult;
import com.vbc.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    PageResult<VideoVO> listVideos(Integer page, Integer pageSize, String keyword, String status, Long deviceId);
    VideoVO getVideo(Long id);
    VideoVO uploadVideo(MultipartFile file, VideoUploadDTO dto);
    void deleteVideo(Long id);
}
