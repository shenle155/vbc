package com.vbc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlarmRuleSaveDTO {
    @NotBlank
    private String ruleName;
    @NotBlank
    private String ruleType;
    private Long videoId;
    private Long zoneId;
    private Integer thresholdValue;
    private Integer durationSeconds;
    private Boolean enabled = true;
    private String alarmLevel = "WARNING";
}
