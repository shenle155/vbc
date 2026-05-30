package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vbc_video")
public class Video {
    @TableId(type = IdType.AUTO)
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
