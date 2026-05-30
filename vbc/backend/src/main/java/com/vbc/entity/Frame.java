package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vbc_frame")
public class Frame {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private Integer frameIndex;
    private Double timestampSeconds;
    private String filePath;
    private Boolean processed;
    private LocalDateTime createdAt;
}
