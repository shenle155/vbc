package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vbc_device")
public class Device {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceName;
    private String deviceCode;
    private String ipAddress;
    private String streamUrl;
    private String location;
    private String status;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
