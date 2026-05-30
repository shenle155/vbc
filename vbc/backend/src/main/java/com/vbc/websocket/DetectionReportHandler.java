package com.vbc.websocket;

import com.vbc.dto.DetectionReportDTO;
import com.vbc.service.DetectionService;
import com.vbc.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DetectionReportHandler {

    private final DetectionService detectionService;
    private final NotificationService notificationService;

    @MessageMapping("/detection/report")
    public void handleDetectionReport(@Payload DetectionReportDTO report) {
        log.info("Received detection report: video={}, frame={}, personCount={}, vehicleCount={}",
                report.getVideoId(), report.getFrameIndex(),
                report.getPersonCount(), report.getVehicleCount());

        // Save detection record
        detectionService.saveDetection(report);

        // Broadcast to subscribers
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "DETECTION_RESULT");
        payload.put("videoId", report.getVideoId());
        payload.put("frameIndex", report.getFrameIndex());
        payload.put("timestampSeconds", report.getTimestampSeconds());
        payload.put("detections", report.getDetections());
        payload.put("personCount", report.getPersonCount());
        payload.put("vehicleCount", report.getVehicleCount());
        payload.put("totalCount", report.getTotalCount());
        payload.put("createdAt", Instant.now().toString());

        notificationService.broadcastToVideo(report.getVideoId(), payload);

        // Also update dashboard stats
        Map<String, Object> statsUpdate = new LinkedHashMap<>();
        statsUpdate.put("type", "STATS_UPDATE");
        statsUpdate.put("timestamp", Instant.now().toEpochMilli());
        notificationService.broadcast("/topic/dashboard", statsUpdate);
    }
}
