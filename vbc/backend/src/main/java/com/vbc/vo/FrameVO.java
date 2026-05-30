package com.vbc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameVO {
    private Long id;
    private Long videoId;
    private Integer frameIndex;
    private Double timestampSeconds;
    private String fileUrl;
    private Boolean processed;
}
