package com.hannah.springssedemo.controller;


import com.hannah.springssedemo.dto.LoginReqDto;
import com.hannah.springssedemo.dto.LoginResDto;
import com.hannah.springssedemo.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResDto login(@RequestBody LoginReqDto loginReqDto) {
        return loginService.login(loginReqDto);
    }
}
