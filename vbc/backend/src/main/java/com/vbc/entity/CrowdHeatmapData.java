package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vbc_crowd_heatmap_data")
public class CrowdHeatmapData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private Double timestampSeconds;
    private Integer gridX;
    private Integer gridY;
    private Double densityValue;
    private LocalDateTime createdAt;
}
