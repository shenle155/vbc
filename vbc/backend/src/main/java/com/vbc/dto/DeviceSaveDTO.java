package com.vbc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceSaveDTO {
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    private String ipAddress;
    private String streamUrl;
    private String location;
}
