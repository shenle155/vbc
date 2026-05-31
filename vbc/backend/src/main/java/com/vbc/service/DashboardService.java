package com.vbc.service;

import com.vbc.vo.*;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardOverviewVO getOverview();
    List<PersonTrendVO> getPersonTrend(String period);
    AlarmStatsVO getAlarmStats();
    List<DeviceStatusVO> getDeviceStatus();
    List<HeatmapDataVO> getHeatmapData(Long videoId, Double startTime, Double endTime);
}
