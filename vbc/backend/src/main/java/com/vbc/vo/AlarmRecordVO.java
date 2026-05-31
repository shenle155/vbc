package com.vbc.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlarmRecordVO {
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
    private String snapshotUrl;
}
