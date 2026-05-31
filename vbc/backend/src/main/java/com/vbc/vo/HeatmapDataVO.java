package com.vbc.vo;

import lombok.Data;

@Data
public class HeatmapDataVO {
    private Double timestampSeconds;
    private int gridX;
    private int gridY;
    private double densityValue;
}
