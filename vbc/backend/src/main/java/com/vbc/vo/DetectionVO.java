package com.vbc.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DetectionVO {
    private Long id;
    private Long videoId;
    private Integer frameIndex;
    private Double timestampSeconds;
    private List<DetectionItemVO> detectedObjects;
    private Integer personCount;
    private Integer vehicleCount;
    private Integer totalCount;
    private LocalDateTime createdAt;

    @Data
    public static class DetectionItemVO {
        private String className;
        private Double confidence;
        private BBoxVO bbox;

        @Data
        public static class BBoxVO {
            private Double x;
            private Double y;
            private Double w;
            private Double h;
        }
    }
}
