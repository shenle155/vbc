package com.vbc.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceStatusVO {
    private Long deviceId;
    private String deviceName;
    private String status;
    private LocalDateTime lastHeartbeat;
}
