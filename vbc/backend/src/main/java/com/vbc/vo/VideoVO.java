package com.vbc.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoVO {
    private Long id;
    private String title;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private Integer durationSeconds;
    private Integer width;
    private Integer height;
    private Double fps;
    private String status;
    private String thumbnailPath;
    private Long deviceId;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
