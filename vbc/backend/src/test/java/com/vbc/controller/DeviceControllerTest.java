package com.vbc.controller;

import com.vbc.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeviceControllerTest extends BaseTest {

    @Test @Order(1) @DisplayName("GET /api/v1/devices - list with pagination")
    void listDevices() throws Exception {
        mockMvc.perform(get("/api/v1/devices?page=1&pageSize=5"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test @Order(2) @DisplayName("GET /api/v1/devices - filter by keyword")
    void listDevicesByKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/devices?keyword=Gate"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.records.length()").value(1));
    }

    @Test @Order(3) @DisplayName("GET /api/v1/devices - filter by status")
    void listDevicesByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/devices?status=ONLINE"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.records.length()").value(1));
    }

    @Test @Order(4) @DisplayName("GET /api/v1/devices/{id} - detail")
    void getDevice() throws Exception {
        mockMvc.perform(get("/api/v1/devices/1"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.deviceName").value("Gate Camera"))
                .andExpect(jsonPath("$.data.deviceCode").value("CAM-001"));
    }

    @Test @Order(5) @DisplayName("GET /api/v1/devices/{id} - not found")
    void getDeviceNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/devices/999"))
                .andExpect(error());
    }

    @Test @Order(6) @DisplayName("POST /api/v1/devices - create")
    void createDevice() throws Exception {
        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceName\":\"New Camera\",\"deviceCode\":\"CAM-003\",\"location\":\"Gate 3\"}"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.deviceName").value("New Camera"))
                .andExpect(jsonPath("$.data.status").value("OFFLINE"));
    }

    @Test @Order(7) @DisplayName("POST /api/v1/devices - duplicate code")
    void createDeviceDuplicateCode() throws Exception {
        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceName\":\"Dup\",\"deviceCode\":\"CAM-003\"}"))
                .andExpect(error());
    }

    @Test @Order(8) @DisplayName("PUT /api/v1/devices/{id} - update")
    void updateDevice() throws Exception {
        mockMvc.perform(put("/api/v1/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceName\":\"Updated Gate\",\"deviceCode\":\"CAM-001\",\"location\":\"New Location\"}"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.deviceName").value("Updated Gate"));
    }

    @Test @Order(9) @DisplayName("POST /api/v1/devices/{id}/heartbeat")
    void heartbeat() throws Exception {
        mockMvc.perform(post("/api/v1/devices/1/heartbeat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ONLINE\"}"))
                .andExpect(success());
    }

    @Test @Order(10) @DisplayName("DELETE /api/v1/devices/{id} - delete")
    void deleteDevice() throws Exception {
        mockMvc.perform(get("/api/v1/devices/3"))
                .andExpect(success());

        mockMvc.perform(delete("/api/v1/devices/3"))
                .andExpect(success());
    }
}
