package com.vbc.controller;

import com.vbc.dto.VideoUploadDTO;
import com.vbc.service.VideoService;
import com.vbc.vo.PageResult;
import com.vbc.vo.VideoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deviceId) {
        PageResult<VideoVO> result = videoService.listVideos(page, pageSize, keyword, status, deviceId);
        return success(result);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return success(videoService.getVideo(id));
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            @Valid @RequestParam("title") String title,
            @RequestParam(value = "deviceId", required = false) Long deviceId) {
        VideoUploadDTO dto = new VideoUploadDTO();
        dto.setTitle(title);
        dto.setDeviceId(deviceId);
        return success(videoService.uploadVideo(file, dto));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return success(null);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        result.put("timestamp", Instant.now().toEpochMilli());
        return result;
    }
}
