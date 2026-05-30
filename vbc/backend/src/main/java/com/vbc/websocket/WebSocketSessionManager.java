package com.vbc.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, String> sessionTokens = new ConcurrentHashMap<>();

    public void registerToken(String token, String sessionId) {
        sessionTokens.put(token, sessionId);
    }

    public void removeToken(String token) {
        sessionTokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return sessionTokens.containsKey(token);
    }

    public String generateToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
