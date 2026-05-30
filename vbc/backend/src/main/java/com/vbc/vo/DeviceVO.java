package com.vbc.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceVO {
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
