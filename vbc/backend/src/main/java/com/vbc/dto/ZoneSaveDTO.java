package com.vbc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ZoneSaveDTO {
    @NotBlank
    private String zoneName;
    private String zoneColor = "#FF0000";
    private List<PointDTO> polygonPoints;

    @Data
    public static class PointDTO {
        private Double x;
        private Double y;
    }
}
