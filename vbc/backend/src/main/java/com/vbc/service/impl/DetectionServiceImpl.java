package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbc.dto.DetectionReportDTO;
import com.vbc.entity.DetectionRecord;
import com.vbc.repository.DetectionRecordMapper;
import com.vbc.service.DetectionService;
import com.vbc.vo.DetectionVO;
import com.vbc.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionServiceImpl implements DetectionService {

    private final DetectionRecordMapper detectionRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void saveDetection(DetectionReportDTO report) {
        DetectionRecord record = new DetectionRecord();
        record.setVideoId(report.getVideoId());
        record.setFrameIndex(report.getFrameIndex());
        record.setTimestampSeconds(report.getTimestampSeconds());

        // Convert detection items to normalized format for JSON storage
        List<DetectionVO.DetectionItemVO> normalizedItems = new ArrayList<>();
        if (report.getDetections() != null) {
            for (var item : report.getDetections()) {
                DetectionVO.DetectionItemVO vo = new DetectionVO.DetectionItemVO();
                vo.setClassName(item.getClassName());
                vo.setConfidence(item.getConfidence());
                DetectionVO.DetectionItemVO.BBoxVO bboxVO = new DetectionVO.DetectionItemVO.BBoxVO();
                if (item.getBbox() != null) {
                    bboxVO.setX(item.getBbox().getX());
                    bboxVO.setY(item.getBbox().getY());
                    bboxVO.setW(item.getBbox().getW());
                    bboxVO.setH(item.getBbox().getH());
                }
                vo.setBbox(bboxVO);
                normalizedItems.add(vo);
            }
        }

        try {
            record.setDetectedObjects(objectMapper.writeValueAsString(normalizedItems));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize detected objects", e);
            record.setDetectedObjects("[]");
        }

        record.setPersonCount(report.getPersonCount() != null ? report.getPersonCount() : 0);
        record.setVehicleCount(report.getVehicleCount() != null ? report.getVehicleCount() : 0);
        record.setTotalCount(report.getTotalCount() != null ? report.getTotalCount()
                : record.getPersonCount() + record.getVehicleCount());

        detectionRecordMapper.insert(record);
    }

    @Override
    public PageResult<DetectionVO> listDetections(Long videoId, Integer page, Integer pageSize,
                                                   String startTime, String endTime) {
        LambdaQueryWrapper<DetectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DetectionRecord::getVideoId, videoId)
                .orderByDesc(DetectionRecord::getCreatedAt);

        Page<DetectionRecord> result = detectionRecordMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<DetectionVO> records = result.getRecords().stream().map(r -> {
            DetectionVO vo = new DetectionVO();
            vo.setId(r.getId());
            vo.setVideoId(r.getVideoId());
            vo.setFrameIndex(r.getFrameIndex());
            vo.setTimestampSeconds(r.getTimestampSeconds());
            vo.setPersonCount(r.getPersonCount());
            vo.setVehicleCount(r.getVehicleCount());
            vo.setTotalCount(r.getTotalCount());
            vo.setCreatedAt(r.getCreatedAt());
            try {
                @SuppressWarnings("unchecked")
                List<DetectionVO.DetectionItemVO> items = objectMapper.readValue(
                        r.getDetectedObjects(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, DetectionVO.DetectionItemVO.class));
                vo.setDetectedObjects(items);
            } catch (JsonProcessingException e) {
                vo.setDetectedObjects(List.of());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }
}
