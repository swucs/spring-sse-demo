package com.hannah.springssedemo.service;

import com.hannah.springssedemo.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(String userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        sseEmitter.onCompletion(() -> {
            log.info("SseEmitter complete");
            sseEmitterRepository.deleteById(userId);
        });

        sseEmitter.onTimeout(() -> {
            log.info("SseEmitter timeout");
            sseEmitterRepository.deleteById(userId);
        });

        sseEmitterRepository.save(userId, sseEmitter);
        this.send(userId, "subscribe", "EventStream Created.");
        return sseEmitter;
    }

    public void send(String userId, String eventName, String message) {
        SseEmitter sseEmitter = sseEmitterRepository.findById(userId);

        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().id(userId)
                        .name(eventName)
                        .data(message));
            } catch (Exception e) {
                log.error("SseEmitter send error", e);
                sseEmitterRepository.deleteById(userId);
                sseEmitter.completeWithError(e);
            }
        }
    }
}
