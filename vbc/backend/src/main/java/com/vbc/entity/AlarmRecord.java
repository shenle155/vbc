package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vbc_alarm_record")
public class AlarmRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ruleId;
    private Long videoId;
    private String alarmType;
    private String alarmLevel;
    private String alarmMessage;
    private String snapshotPath;
    private Integer frameIndex;
    private Double timestampSeconds;
    private Boolean handled;
    private String handledBy;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
