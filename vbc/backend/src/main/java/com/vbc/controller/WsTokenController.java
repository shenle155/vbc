package com.vbc.controller;

import com.vbc.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ws")
@RequiredArgsConstructor
public class WsTokenController {

    private final WebSocketSessionManager sessionManager;

    @GetMapping("/token")
    public Map<String, Object> getToken() {
        String token = sessionManager.generateToken();
        sessionManager.registerToken(token, null);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("brokerUrl", "/ws");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        result.put("timestamp", Instant.now().toEpochMilli());
        return result;
    }
}
