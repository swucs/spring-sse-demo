package com.hannah.springssedemo.repository;


import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {
    private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    public void save(String userId, SseEmitter sseEmitter) {
        sseEmitters.put(userId, sseEmitter);
    }

    public void deleteById(String userId) {
        sseEmitters.remove(userId);
    }

    public SseEmitter findById(String userId) {
        return sseEmitters.get(userId);
    }
}
