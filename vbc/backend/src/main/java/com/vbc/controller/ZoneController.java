package com.vbc.controller;

import com.vbc.dto.ZoneSaveDTO;
import com.vbc.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/videos/{videoId}/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping
    public Map<String, Object> list(@PathVariable Long videoId) {
        return success(zoneService.listZones(videoId));
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable Long videoId, @Valid @RequestBody ZoneSaveDTO dto) {
        return success(zoneService.createZone(videoId, dto));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody ZoneSaveDTO dto) {
        return success(zoneService.updateZone(id, dto));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return success(null);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "success");
        r.put("data", data); r.put("timestamp", Instant.now().toEpochMilli());
        return r;
    }
}
