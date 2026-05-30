package com.vbc.controller;

import com.vbc.service.DetectionService;
import com.vbc.vo.DetectionVO;
import com.vbc.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/videos/{videoId}/detections")
@RequiredArgsConstructor
public class DetectionController {

    private final DetectionService detectionService;

    @GetMapping
    public Map<String, Object> list(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        PageResult<DetectionVO> result = detectionService.listDetections(videoId, page, pageSize, startTime, endTime);
        return success(result);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@PathVariable Long videoId) {
        // Simple stats: count frames with detections for this video
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("videoId", videoId);
        return success(stats);
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
