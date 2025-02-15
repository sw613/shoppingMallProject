package com.project.elice2.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "이메일 또는 비밀번호가 잘못되었습니다.";

        // 예외 종류에 따라 메시지 설정
        if (exception instanceof AccountExpiredException) {
            errorMessage = "탈퇴한 회원입니다.";
        } else if (exception.getMessage().contains("Bad credentials")) {
            errorMessage = "이메일 또는 비밀번호가 잘못되었습니다.";
        }

        // 메시지 URL 인코딩
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 실패 메시지를 URL 파라미터로 전달
        response.sendRedirect("/login?error=" + encodedMessage);
    }
}