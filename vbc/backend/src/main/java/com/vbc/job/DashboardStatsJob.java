package com.vbc.job;

import com.vbc.service.DashboardService;
import com.vbc.service.NotificationService;
import com.vbc.vo.DashboardOverviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardStatsJob {

    private final DashboardService dashboardService;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 30000)
    public void pushStats() {
        try {
            DashboardOverviewVO overview = dashboardService.getOverview();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("type", "STATS_UPDATE");
            payload.put("onlineDevices", overview.getOnlineDevices());
            payload.put("totalDevices", overview.getTotalDevices());
            payload.put("todayAlarms", overview.getTodayAlarms());
            payload.put("unhandledAlarms", overview.getUnhandledAlarms());
            payload.put("todayPersonCount", overview.getTodayPersonCount());
            payload.put("todayVehicleCount", overview.getTodayVehicleCount());
            payload.put("timestamp", Instant.now().toEpochMilli());
            notificationService.broadcast("/topic/dashboard", payload);
        } catch (Exception e) {
            log.error("Dashboard stats push failed", e);
        }
    }
}
