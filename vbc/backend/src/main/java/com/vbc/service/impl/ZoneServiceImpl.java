package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbc.dto.ZoneSaveDTO;
import com.vbc.entity.Zone;
import com.vbc.repository.ZoneMapper;
import com.vbc.service.ZoneService;
import com.vbc.vo.ZoneVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneMapper zoneMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<ZoneVO> listZones(Long videoId) {
        LambdaQueryWrapper<Zone> w = new LambdaQueryWrapper<>();
        w.eq(Zone::getVideoId, videoId).orderByAsc(Zone::getCreatedAt);
        return zoneMapper.selectList(w).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public ZoneVO createZone(Long videoId, ZoneSaveDTO dto) {
        Zone zone = new Zone();
        zone.setVideoId(videoId);
        zone.setZoneName(dto.getZoneName());
        zone.setZoneColor(dto.getZoneColor());
        try {
            zone.setPolygonPoints(objectMapper.writeValueAsString(dto.getPolygonPoints()));
        } catch (JsonProcessingException e) {
            zone.setPolygonPoints("[]");
        }
        zoneMapper.insert(zone);
        return toVO(zone);
    }

    @Override
    public ZoneVO updateZone(Long id, ZoneSaveDTO dto) {
        Zone zone = zoneMapper.selectById(id);
        if (zone == null) throw new IllegalArgumentException("区域不存在");
        BeanUtils.copyProperties(dto, zone, "polygonPoints");
        try {
            if (dto.getPolygonPoints() != null) {
                zone.setPolygonPoints(objectMapper.writeValueAsString(dto.getPolygonPoints()));
            }
        } catch (JsonProcessingException ignored) {}
        zoneMapper.updateById(zone);
        return toVO(zone);
    }

    @Override
    public void deleteZone(Long id) {
        zoneMapper.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    private ZoneVO toVO(Zone z) {
        ZoneVO vo = new ZoneVO();
        vo.setId(z.getId());
        vo.setZoneName(z.getZoneName());
        vo.setVideoId(z.getVideoId());
        vo.setZoneColor(z.getZoneColor());
        vo.setCreatedAt(z.getCreatedAt());
        try {
            List<ZoneVO.PointVO> points = objectMapper.readValue(z.getPolygonPoints(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ZoneVO.PointVO.class));
            vo.setPolygonPoints(points);
        } catch (Exception e) {
            vo.setPolygonPoints(List.of());
        }
        return vo;
    }
}
