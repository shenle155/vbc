package com.vbc.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetectionReportDTO {
    private Long videoId;
    private Integer frameIndex;
    private Double timestampSeconds;
    private List<DetectionItem> detections;
    private Integer personCount;
    private Integer vehicleCount;
    private Integer totalCount;

    @Data
    public static class DetectionItem {
        private String className;
        private Double confidence;
        private BBox bbox;

        @Data
        public static class BBox {
            private Double x;
            private Double y;
            private Double w;
            private Double h;
        }
    }
}
