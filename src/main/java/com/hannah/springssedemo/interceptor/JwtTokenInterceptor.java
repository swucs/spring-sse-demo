package com.hannah.springssedemo.interceptor;

import com.hannah.springssedemo.entity.User;
import com.hannah.springssedemo.repository.UserRepository;
import com.hannah.springssedemo.threadlocal.CurrentUser;
import com.hannah.springssedemo.threadlocal.CurrentUserContextHolder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserRepository userRepository;



    /**
     * 요청이 들어올 때마다 실행되는 메소드
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            //1. 토큰이 유효한지 체크
            String accessToken = this.resolveToken(request);
            if (accessToken == null) {
                throw new RuntimeException("Invalid Token");
            }

            String userId = this.parseToken(accessToken);
            User user = userRepository.findById(userId);

            if (user == null) {
                throw new RuntimeException("Invalid Token");
            }

            //4. 토큰이 유효하면 해당 User정보를 ThreadLocal에 저장
            CurrentUserContextHolder.set(new CurrentUser(user));


        } catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException : {}", e.getMessage());
            this.setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage());
            return false;
        } catch (Exception e) {
            log.info("Invalid Token : {}", e.getMessage());
            this.setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage());
            return false;
        }

        return true;
    }


    // 토큰에서 userId 추출하는 메서드
    private String parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 정보를 가져옴
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) { // 토큰이 null이 아니고, Bearer로 시작하는 경우
            return bearerToken.substring(7); // "Bearer " 이후의 문자열을 반환
        }
        return null;
    }


    /**
     * Controller 요청이 끝날 때마다 실행되는 메소드
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //ThreadLocal에 저장된 User정보를 제거
        CurrentUserContextHolder.clear();
    }



    private void setErrorResponse(HttpStatus httpStatus, HttpServletResponse response, String message) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
