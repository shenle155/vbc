package com.vbc.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlarmRuleVO {
    private Long id;
    private String ruleName;
    private String ruleType;
    private Long videoId;
    private Long zoneId;
    private Integer thresholdValue;
    private Integer durationSeconds;
    private Boolean enabled;
    private String alarmLevel;
    private LocalDateTime createdAt;
}
