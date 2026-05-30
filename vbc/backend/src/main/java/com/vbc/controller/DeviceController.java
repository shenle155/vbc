package com.vbc.controller;

import com.vbc.dto.DeviceHeartbeatDTO;
import com.vbc.dto.DeviceSaveDTO;
import com.vbc.service.DeviceService;
import com.vbc.vo.DeviceVO;
import com.vbc.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        PageResult<DeviceVO> result = deviceService.listDevices(page, pageSize, keyword, status);
        return success(result);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return success(deviceService.getDevice(id));
    }

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody DeviceSaveDTO dto) {
        return success(deviceService.createDevice(dto));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody DeviceSaveDTO dto) {
        return success(deviceService.updateDevice(id, dto));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return success(null);
    }

    @PostMapping("/{id}/heartbeat")
    public Map<String, Object> heartbeat(@PathVariable Long id, @RequestBody DeviceHeartbeatDTO dto) {
        deviceService.heartbeat(id, dto != null ? dto.getStatus() : null);
        return success(null);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        result.put("timestamp", Instant.now().toEpochMilli());
        return result;
    }
}
