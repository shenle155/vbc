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
class AlarmControllerTest extends BaseTest {

    @Test @Order(1) @DisplayName("GET /api/v1/alarms/rules - empty list")
    void listRulesEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/alarms/rules"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test @Order(2) @DisplayName("POST /api/v1/alarms/rules - create")
    void createRule() throws Exception {
        mockMvc.perform(post("/api/v1/alarms/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ruleName\":\"People Exceed\",\"ruleType\":\"PEOPLE_EXCEED\",\"thresholdValue\":10,\"alarmLevel\":\"WARNING\"}"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.ruleName").value("People Exceed"))
                .andExpect(jsonPath("$.data.enabled").value(true));
    }

    @Test @Order(3) @DisplayName("GET /api/v1/alarms/rules - after create")
    void listRulesAfterCreate() throws Exception {
        mockMvc.perform(get("/api/v1/alarms/rules"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test @Order(4) @DisplayName("PUT /api/v1/alarms/rules/{id} - update")
    void updateRule() throws Exception {
        mockMvc.perform(put("/api/v1/alarms/rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ruleName\":\"Updated Rule\",\"ruleType\":\"PEOPLE_EXCEED\",\"thresholdValue\":15,\"alarmLevel\":\"CRITICAL\"}"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.ruleName").value("Updated Rule"))
                .andExpect(jsonPath("$.data.alarmLevel").value("CRITICAL"));
    }

    @Test @Order(5) @DisplayName("GET /api/v1/alarms - alarm records list")
    void listRecords() throws Exception {
        mockMvc.perform(get("/api/v1/alarms"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test @Order(6) @DisplayName("DELETE /api/v1/alarms/rules/{id} - delete")
    void deleteRule() throws Exception {
        mockMvc.perform(delete("/api/v1/alarms/rules/1"))
                .andExpect(success());
    }
}
