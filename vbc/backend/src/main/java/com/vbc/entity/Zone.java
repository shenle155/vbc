package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vbc_zone")
public class Zone {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String zoneName;
    private Long videoId;
    private String zoneColor;
    private String polygonPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
