package com.vbc.service;

import com.vbc.dto.AlarmRuleSaveDTO;
import com.vbc.vo.AlarmRecordVO;
import com.vbc.vo.AlarmRuleVO;
import com.vbc.vo.PageResult;

public interface AlarmService {
    // Rules
    PageResult<AlarmRuleVO> listRules(Integer page, Integer pageSize, Boolean enabled);
    AlarmRuleVO createRule(AlarmRuleSaveDTO dto);
    AlarmRuleVO updateRule(Long id, AlarmRuleSaveDTO dto);
    void deleteRule(Long id);

    // Records
    PageResult<AlarmRecordVO> listRecords(Integer page, Integer pageSize,
        String alarmType, String alarmLevel, Boolean handled, String startDate, String endDate);
    AlarmRecordVO getRecord(Long id);
    void handleRecord(Long id, String handledBy);
}
