package com.vbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vbc.dto.AlarmRuleSaveDTO;
import com.vbc.entity.AlarmRecord;
import com.vbc.entity.AlarmRule;
import com.vbc.repository.AlarmRecordMapper;
import com.vbc.repository.AlarmRuleMapper;
import com.vbc.service.AlarmService;
import com.vbc.util.FileUtil;
import com.vbc.vo.AlarmRecordVO;
import com.vbc.vo.AlarmRuleVO;
import com.vbc.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRuleMapper ruleMapper;
    private final AlarmRecordMapper recordMapper;

    // --- Rules ---
    @Override
    public PageResult<AlarmRuleVO> listRules(Integer page, Integer pageSize, Boolean enabled) {
        LambdaQueryWrapper<AlarmRule> w = new LambdaQueryWrapper<>();
        if (enabled != null) w.eq(AlarmRule::getEnabled, enabled);
        w.orderByDesc(AlarmRule::getCreatedAt);
        Page<AlarmRule> result = ruleMapper.selectPage(new Page<>(page, pageSize), w);
        List<AlarmRuleVO> records = result.getRecords().stream().map(r -> {
            AlarmRuleVO vo = new AlarmRuleVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }

    @Override
    public AlarmRuleVO createRule(AlarmRuleSaveDTO dto) {
        AlarmRule rule = new AlarmRule();
        BeanUtils.copyProperties(dto, rule);
        rule.setNotifyMethods("[\"websocket\"]");
        ruleMapper.insert(rule);
        AlarmRuleVO vo = new AlarmRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }

    @Override
    public AlarmRuleVO updateRule(Long id, AlarmRuleSaveDTO dto) {
        AlarmRule rule = ruleMapper.selectById(id);
        if (rule == null) throw new IllegalArgumentException("规则不存在");
        BeanUtils.copyProperties(dto, rule);
        ruleMapper.updateById(rule);
        AlarmRuleVO vo = new AlarmRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }

    @Override
    public void deleteRule(Long id) {
        ruleMapper.deleteById(id);
    }

    // --- Records ---
    @Override
    public PageResult<AlarmRecordVO> listRecords(Integer page, Integer pageSize,
            String alarmType, String alarmLevel, Boolean handled, String startDate, String endDate) {
        LambdaQueryWrapper<AlarmRecord> w = new LambdaQueryWrapper<>();
        if (alarmType != null) w.eq(AlarmRecord::getAlarmType, alarmType);
        if (alarmLevel != null) w.eq(AlarmRecord::getAlarmLevel, alarmLevel);
        if (handled != null) w.eq(AlarmRecord::getHandled, handled);
        w.orderByDesc(AlarmRecord::getCreatedAt);
        Page<AlarmRecord> result = recordMapper.selectPage(new Page<>(page, pageSize), w);
        List<AlarmRecordVO> records = result.getRecords().stream().map(this::toRecordVO).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize(), result.getPages());
    }

    @Override
    public AlarmRecordVO getRecord(Long id) {
        AlarmRecord r = recordMapper.selectById(id);
        return r != null ? toRecordVO(r) : null;
    }

    @Override
    public void handleRecord(Long id, String handledBy) {
        AlarmRecord r = recordMapper.selectById(id);
        if (r != null) {
            r.setHandled(true);
            r.setHandledBy(handledBy);
            r.setHandledAt(LocalDateTime.now());
            recordMapper.updateById(r);
        }
    }

    private AlarmRecordVO toRecordVO(AlarmRecord r) {
        AlarmRecordVO vo = new AlarmRecordVO();
        BeanUtils.copyProperties(r, vo);
        if (r.getSnapshotPath() != null) {
            vo.setSnapshotUrl("/api/v1/files/snapshots/" + FileUtil.getFileName(r.getSnapshotPath()));
        }
        return vo;
    }
}
