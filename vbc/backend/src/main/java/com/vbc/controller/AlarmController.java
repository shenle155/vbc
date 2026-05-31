package com.vbc.controller;

import com.vbc.dto.AlarmRuleSaveDTO;
import com.vbc.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // Rules
    @GetMapping("/rules")
    public Map<String, Object> listRules(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Boolean enabled) {
        return success(alarmService.listRules(page, pageSize, enabled));
    }

    @PostMapping("/rules")
    public Map<String, Object> createRule(@Valid @RequestBody AlarmRuleSaveDTO dto) {
        return success(alarmService.createRule(dto));
    }

    @PutMapping("/rules/{id}")
    public Map<String, Object> updateRule(@PathVariable Long id, @Valid @RequestBody AlarmRuleSaveDTO dto) {
        return success(alarmService.updateRule(id, dto));
    }

    @DeleteMapping("/rules/{id}")
    public Map<String, Object> deleteRule(@PathVariable Long id) {
        alarmService.deleteRule(id);
        return success(null);
    }

    // Records
    @GetMapping
    public Map<String, Object> listRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String alarmLevel,
            @RequestParam(required = false) Boolean handled,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return success(alarmService.listRecords(page, pageSize, alarmType, alarmLevel, handled, startDate, endDate));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getRecord(@PathVariable Long id) {
        return success(alarmService.getRecord(id));
    }

    @PutMapping("/{id}/handle")
    public Map<String, Object> handleRecord(@PathVariable Long id, @RequestBody Map<String, String> body) {
        alarmService.handleRecord(id, body.getOrDefault("handledBy", "admin"));
        return success(null);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "success");
        r.put("data", data); r.put("timestamp", Instant.now().toEpochMilli());
        return r;
    }
}
