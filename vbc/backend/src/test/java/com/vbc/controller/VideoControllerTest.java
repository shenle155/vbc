package com.vbc.controller;

import com.vbc.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VideoControllerTest extends BaseTest {

    @Test @Order(1) @DisplayName("GET /api/v1/videos - list")
    void listVideos() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test @Order(2) @DisplayName("GET /api/v1/videos/{id} - detail")
    void getVideo() throws Exception {
        mockMvc.perform(get("/api/v1/videos/1"))
                .andExpect(success())
                .andExpect(jsonPath("$.data.title").value("Test Video 1"))
                .andExpect(jsonPath("$.data.status").value("READY"));
    }

    @Test @Order(3) @DisplayName("GET /api/v1/videos/{id}/frames - empty when no frames")
    void listFrames() throws Exception {
        mockMvc.perform(get("/api/v1/videos/1/frames?startIndex=0&endIndex=10"))
                .andExpect(success());
    }

    @Test @Order(4) @DisplayName("POST /api/v1/videos/{id}/extract - triggers extraction")
    void extractFrames() throws Exception {
        mockMvc.perform(post("/api/v1/videos/1/extract")
                        .contentType("application/json")
                        .content("{\"intervalSeconds\":1.0,\"maxFrames\":10}"))
                .andExpect(success());
    }

    @Test @Order(5) @DisplayName("GET /api/v1/videos/{id}/detections - empty")
    void listDetections() throws Exception {
        mockMvc.perform(get("/api/v1/videos/1/detections"))
                .andExpect(success());
    }

    @Test @Order(6) @DisplayName("DELETE /api/v1/videos/{id} - delete")
    void deleteVideo() throws Exception {
        mockMvc.perform(delete("/api/v1/videos/1"))
                .andExpect(success());
    }
}
