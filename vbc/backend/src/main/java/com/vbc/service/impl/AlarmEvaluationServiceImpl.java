package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbc.entity.*;
import com.vbc.repository.*;
import com.vbc.service.AlarmEvaluationService;
import com.vbc.service.NotificationService;
import com.vbc.util.PolygonUtil;
import com.vbc.vo.ZoneVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmEvaluationServiceImpl implements AlarmEvaluationService {

    private final AlarmRuleMapper ruleMapper;
    private final AlarmRecordMapper recordMapper;
    private final ZoneMapper zoneMapper;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public void evaluate(DetectionRecord record) {
        LambdaQueryWrapper<AlarmRule> w = new LambdaQueryWrapper<>();
        w.eq(AlarmRule::getEnabled, true)
                .and(wr -> wr.eq(AlarmRule::getVideoId, record.getVideoId()).or().isNull(AlarmRule::getVideoId));
        List<AlarmRule> rules = ruleMapper.selectList(w);

        for (AlarmRule rule : rules) {
            try {
                String msg = evaluateRule(rule, record);
                if (msg != null) {
                    triggerAlarm(rule, record, msg);
                }
            } catch (Exception e) {
                log.error("Alarm evaluation error for rule {}", rule.getId(), e);
            }
        }
    }

    private String evaluateRule(AlarmRule rule, DetectionRecord record) {
        return switch (rule.getRuleType()) {
            case "PEOPLE_EXCEED" -> {
                if (rule.getThresholdValue() != null && record.getPersonCount() > rule.getThresholdValue()) {
                    yield "人数超限: " + record.getPersonCount() + " 人 (阈值: " + rule.getThresholdValue() + ")";
                }
                yield null;
            }
            case "ZONE_INTRUSION" -> checkZoneIntrusion(rule, record);
            case "CROWD_GATHERING" -> checkCrowdGathering(rule, record);
            default -> null;
        };
    }

    private String checkZoneIntrusion(AlarmRule rule, DetectionRecord record) {
        if (rule.getZoneId() == null) return null;
        List<PolygonUtil.Point> polygon = loadPolygon(rule.getZoneId());
        if (polygon.isEmpty()) return null;

        var detections = parseDetections(record.getDetectedObjects());
        for (var det : detections) {
            Map<String, Object> d = (Map<String, Object>) det;
            if (!"person".equals(d.get("className"))) continue;
            Map<String, Object> bbox = (Map<String, Object>) d.get("bbox");
            if (bbox == null) continue;
            double cx = toDouble(bbox.get("x")) + toDouble(bbox.get("w")) / 2;
            double cy = toDouble(bbox.get("y")) + toDouble(bbox.get("h")) / 2;
            if (PolygonUtil.isPointInPolygon(cx, cy, polygon)) {
                Zone zone = zoneMapper.selectById(rule.getZoneId());
                return "区域入侵: " + (zone != null ? zone.getZoneName() : "未知区域");
            }
        }
        return null;
    }

    private String checkCrowdGathering(AlarmRule rule, DetectionRecord record) {
        if (rule.getZoneId() == null) return null;
        if (rule.getThresholdValue() == null) return null;
        List<PolygonUtil.Point> polygon = loadPolygon(rule.getZoneId());
        if (polygon.isEmpty()) return null;

        int count = 0;
        var detections = parseDetections(record.getDetectedObjects());
        for (var det : detections) {
            Map<String, Object> d = (Map<String, Object>) det;
            if (!"person".equals(d.get("className"))) continue;
            Map<String, Object> bbox = (Map<String, Object>) d.get("bbox");
            if (bbox == null) continue;
            double cx = toDouble(bbox.get("x")) + toDouble(bbox.get("w")) / 2;
            double cy = toDouble(bbox.get("y")) + toDouble(bbox.get("h")) / 2;
            if (PolygonUtil.isPointInPolygon(cx, cy, polygon)) count++;
        }
        if (count > rule.getThresholdValue()) {
            Zone zone = zoneMapper.selectById(rule.getZoneId());
            return "人群聚集: " + count + " 人在 " + (zone != null ? zone.getZoneName() : "区域") + " (阈值: " + rule.getThresholdValue() + ")";
        }
        return null;
    }

    private List<PolygonUtil.Point> loadPolygon(Long zoneId) {
        Zone zone = zoneMapper.selectById(zoneId);
        if (zone == null) return List.of();
        try {
            List<ZoneVO.PointVO> points = objectMapper.readValue(zone.getPolygonPoints(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ZoneVO.PointVO.class));
            return points.stream().map(p -> new PolygonUtil.Point(p.getX(), p.getY())).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<?> parseDetections(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private double toDouble(Object v) {
        return v instanceof Number n ? n.doubleValue() : 0;
    }

    private void triggerAlarm(AlarmRule rule, DetectionRecord record, String message) {
        AlarmRecord alarm = new AlarmRecord();
        alarm.setRuleId(rule.getId());
        alarm.setVideoId(record.getVideoId());
        alarm.setAlarmType(rule.getRuleType());
        alarm.setAlarmLevel(rule.getAlarmLevel());
        alarm.setAlarmMessage(message);
        alarm.setFrameIndex(record.getFrameIndex());
        alarm.setTimestampSeconds(record.getTimestampSeconds());
        alarm.setHandled(false);
        recordMapper.insert(alarm);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "ALARM_TRIGGERED");
        payload.put("alarmId", alarm.getId());
        payload.put("videoId", record.getVideoId());
        payload.put("alarmType", rule.getRuleType());
        payload.put("alarmLevel", rule.getAlarmLevel());
        payload.put("alarmMessage", message);
        payload.put("ruleId", rule.getId());
        payload.put("ruleName", rule.getRuleName());
        payload.put("frameIndex", record.getFrameIndex());
        payload.put("timestampSeconds", record.getTimestampSeconds());
        payload.put("createdAt", Instant.now().toString());

        notificationService.broadcast("/topic/alarm/" + record.getVideoId(), payload);
    }
}
