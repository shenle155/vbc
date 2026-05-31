package com.vbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vbc_alarm_rule")
public class AlarmRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String ruleType;
    private Long videoId;
    private Long zoneId;
    private Integer thresholdValue;
    private Integer durationSeconds;
    private Boolean enabled;
    private String alarmLevel;
    private String notifyMethods;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
