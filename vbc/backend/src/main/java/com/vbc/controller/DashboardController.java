package com.vbc.controller;

import com.vbc.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return success(dashboardService.getOverview());
    }

    @GetMapping("/person-trend")
    public Map<String, Object> personTrend(@RequestParam(defaultValue = "HOURLY") String period) {
        return success(dashboardService.getPersonTrend(period));
    }

    @GetMapping("/alarm-stats")
    public Map<String, Object> alarmStats() {
        return success(dashboardService.getAlarmStats());
    }

    @GetMapping("/device-status")
    public Map<String, Object> deviceStatus() {
        return success(dashboardService.getDeviceStatus());
    }

    @GetMapping("/heatmap-data/{videoId}")
    public Map<String, Object> heatmapData(@PathVariable Long videoId,
            @RequestParam(defaultValue = "0") Double startTime,
            @RequestParam(defaultValue = "60") Double endTime) {
        return success(dashboardService.getHeatmapData(videoId, startTime, endTime));
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "success");
        r.put("data", data); r.put("timestamp", Instant.now().toEpochMilli());
        return r;
    }
}
