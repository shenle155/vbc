package com.vbc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoUploadDTO {
    @NotBlank(message = "视频标题不能为空")
    private String title;
    private Long deviceId;
}
