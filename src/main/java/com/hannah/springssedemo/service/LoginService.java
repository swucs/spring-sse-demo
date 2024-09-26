package com.hannah.springssedemo.service;


import com.hannah.springssedemo.dto.LoginReqDto;
import com.hannah.springssedemo.dto.LoginResDto;
import com.hannah.springssedemo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final UserRepository userRepository;

    public LoginResDto login(LoginReqDto reqDto) {
        log.info("loginReqDto : {}", reqDto);

        if (userRepository.findById(reqDto.getUserId()) == null) {
            throw new IllegalArgumentException("Invalid userId");
        }

        if (!userRepository.findById(reqDto.getUserId()).getPassword().equals(reqDto.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String accessToken = this.createAccessToken(reqDto.getUserId());
        return new LoginResDto(accessToken);
    }


    private String createAccessToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime); // 토큰 유효시간 설정

        // 1. secretKey를 바이트 배열로 변환 (signWith 메서드 기존의 방식을 deprecated함)
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        // 2. 바이트 배열로 SecretKeySpec 객체 생성
        Key key = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, HMAC_SHA256);

        return Jwts.builder()
                .setClaims(Jwts.claims().setSubject(userId)) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expireDate) // set Expire Time
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact(); // 직렬화하여 문자열로 변환
    }

}
