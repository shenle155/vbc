package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vbc.entity.*;
import com.vbc.repository.*;
import com.vbc.service.DashboardService;
import com.vbc.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DeviceMapper deviceMapper;
    private final VideoMapper videoMapper;
    private final DetectionRecordMapper detectionRecordMapper;
    private final AlarmRecordMapper alarmRecordMapper;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        vo.setTotalDevices((int) deviceMapper.selectCount(null));
        vo.setOnlineDevices((int) deviceMapper.selectCount(
                new LambdaQueryWrapper<Device>().eq(Device::getStatus, "ONLINE")));
        vo.setActiveVideos((int) videoMapper.selectCount(
                new LambdaQueryWrapper<Video>().eq(Video::getStatus, "READY")));

        LocalDate today = LocalDate.now();
        vo.setTodayAlarms((int) alarmRecordMapper.selectCount(
                new LambdaQueryWrapper<AlarmRecord>()
                        .ge(AlarmRecord::getCreatedAt, today.atStartOfDay())));
        vo.setUnhandledAlarms((int) alarmRecordMapper.selectCount(
                new LambdaQueryWrapper<AlarmRecord>()
                        .eq(AlarmRecord::getHandled, false)
                        .ge(AlarmRecord::getCreatedAt, today.atStartOfDay())));

        // Sum counts from today's detections
        var detWrapper = new LambdaQueryWrapper<DetectionRecord>()
                .ge(DetectionRecord::getCreatedAt, today.atStartOfDay());
        List<DetectionRecord> todayDetections = detectionRecordMapper.selectList(detWrapper);
        vo.setTodayPersonCount(todayDetections.stream().mapToInt(DetectionRecord::getPersonCount).sum());
        vo.setTodayVehicleCount(todayDetections.stream().mapToInt(DetectionRecord::getVehicleCount).sum());

        return vo;
    }

    @Override
    public List<PersonTrendVO> getPersonTrend(String period) {
        // Simplified: aggregate last 24 hours by hour
        List<PersonTrendVO> list = new ArrayList<>();
        var now = java.time.LocalDateTime.now();
        for (int i = 23; i >= 0; i--) {
            var hour = now.minusHours(i).withMinute(0).withSecond(0).withNano(0);
            var nextHour = hour.plusHours(1);
            var w = new LambdaQueryWrapper<DetectionRecord>()
                    .ge(DetectionRecord::getCreatedAt, hour)
                    .lt(DetectionRecord::getCreatedAt, nextHour);
            List<DetectionRecord> detections = detectionRecordMapper.selectList(w);
            PersonTrendVO vo = new PersonTrendVO();
            vo.setTime(String.format("%02d:00", hour.getHour()));
            vo.setPersonCount(detections.stream().mapToInt(DetectionRecord::getPersonCount).sum());
            vo.setVehicleCount(detections.stream().mapToInt(DetectionRecord::getVehicleCount).sum());
            list.add(vo);
        }
        return list;
    }

    @Override
    public AlarmStatsVO getAlarmStats() {
        AlarmStatsVO vo = new AlarmStatsVO();

        // By type
        List<AlarmStatsVO.TypeCount> byType = new ArrayList<>();
        for (String type : new String[]{"ZONE_INTRUSION", "CROWD_GATHERING", "PEOPLE_EXCEED"}) {
            AlarmStatsVO.TypeCount tc = new AlarmStatsVO.TypeCount();
            tc.setType(type);
            tc.setCount(alarmRecordMapper.selectCount(
                    new LambdaQueryWrapper<AlarmRecord>().eq(AlarmRecord::getAlarmType, type)));
            byType.add(tc);
        }
        vo.setByType(byType);

        // By level
        List<AlarmStatsVO.LevelCount> byLevel = new ArrayList<>();
        for (String level : new String[]{"INFO", "WARNING", "CRITICAL"}) {
            AlarmStatsVO.LevelCount lc = new AlarmStatsVO.LevelCount();
            lc.setLevel(level);
            lc.setCount(alarmRecordMapper.selectCount(
                    new LambdaQueryWrapper<AlarmRecord>().eq(AlarmRecord::getAlarmLevel, level)));
            byLevel.add(lc);
        }
        vo.setByLevel(byLevel);

        // Trend (last 7 days)
        List<AlarmStatsVO.TrendItem> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            var day = LocalDate.now().minusDays(i);
            AlarmStatsVO.TrendItem ti = new AlarmStatsVO.TrendItem();
            ti.setDate(day.toString().substring(5));
            ti.setCount(alarmRecordMapper.selectCount(
                    new LambdaQueryWrapper<AlarmRecord>()
                            .ge(AlarmRecord::getCreatedAt, day.atStartOfDay())
                            .lt(AlarmRecord::getCreatedAt, day.plusDays(1).atStartOfDay())));
            trend.add(ti);
        }
        vo.setTrend(trend);

        return vo;
    }

    @Override
    public List<DeviceStatusVO> getDeviceStatus() {
        List<Device> devices = deviceMapper.selectList(null);
        List<DeviceStatusVO> list = new ArrayList<>();
        for (Device d : devices) {
            DeviceStatusVO vo2 = new DeviceStatusVO();
            vo2.setDeviceId(d.getId());
            vo2.setDeviceName(d.getDeviceName());
            vo2.setStatus(d.getStatus());
            vo2.setLastHeartbeat(d.getLastHeartbeat());
            list.add(vo2);
        }
        return list;
    }

    @Override
    public List<HeatmapDataVO> getHeatmapData(Long videoId, Double startTime, Double endTime) {
        return List.of(); // placeholder for Phase 8
    }
}
