package com.vbc.controller;

import com.vbc.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardControllerTest extends BaseTest {

    @Test @DisplayName("GET /api/v1/dashboard/overview")
    void overview() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/overview"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.onlineDevices").isNumber())
                .andExpect(jsonPath("$.data.totalDevices").isNumber())
                .andExpect(jsonPath("$.data.todayAlarms").isNumber())
                .andExpect(jsonPath("$.data.activeVideos").isNumber());
    }

    @Test @DisplayName("GET /api/v1/dashboard/person-trend")
    void personTrend() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/person-trend"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.length()").value(24));
    }

    @Test @DisplayName("GET /api/v1/dashboard/alarm-stats")
    void alarmStats() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/alarm-stats"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.byType.length()").value(3))
                .andExpect(jsonPath("$.data.byLevel.length()").value(3))
                .andExpect(jsonPath("$.data.trend.length()").value(7));
    }

    @Test @DisplayName("GET /api/v1/dashboard/device-status")
    void deviceStatus() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/device-status"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
