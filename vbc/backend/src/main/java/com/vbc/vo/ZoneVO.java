package com.vbc.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ZoneVO {
    private Long id;
    private String zoneName;
    private Long videoId;
    private String zoneColor;
    private List<PointVO> polygonPoints;
    private LocalDateTime createdAt;

    @Data
    public static class PointVO {
        private double x;
        private double y;
    }
}
