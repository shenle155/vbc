package com.vbc.controller;

import com.vbc.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WsTokenControllerTest extends BaseTest {

    @Test @DisplayName("GET /api/v1/ws/token - returns token and brokerUrl")
    void getToken() throws Exception {
        mockMvc.perform(get("/api/v1/ws/token"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.brokerUrl").value("/ws"));
    }
}
