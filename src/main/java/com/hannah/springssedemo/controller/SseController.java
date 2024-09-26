package com.hannah.springssedemo.controller;


import com.hannah.springssedemo.dto.LoginReqDto;
import com.hannah.springssedemo.dto.LoginResDto;
import com.hannah.springssedemo.dto.SsePushReqDto;
import com.hannah.springssedemo.service.LoginService;
import com.hannah.springssedemo.service.NotificationService;
import com.hannah.springssedemo.threadlocal.CurrentUser;
import com.hannah.springssedemo.threadlocal.CurrentUserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final NotificationService notificationService;

    @PostMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        CurrentUser currentUser = CurrentUserContextHolder.get();
        return notificationService.subscribe(currentUser.getUserId());
    }

    @PostMapping("/push")
    public void push(@RequestBody SsePushReqDto reqDto) {
        reqDto.getUserIds().forEach(userId -> {
            notificationService.send(userId, "push", reqDto.getMessage());
        });
    }
}
