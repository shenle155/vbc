package com.vbc.dto;

import lombok.Data;

@Data
public class FrameExtractDTO {
    private Double intervalSeconds = 1.0;
    private Integer maxFrames = 300;
}
