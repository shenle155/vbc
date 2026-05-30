package com.vbc.service;

public interface NotificationService {
    void broadcast(String destination, Object payload);
    void broadcastToVideo(Long videoId, Object payload);
}
