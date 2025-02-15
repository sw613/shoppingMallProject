package com.project.elice2.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomOAuth2AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "구글 로그인 중 문제가 발생했습니다.";

        // provider가 LOCAL인 경우 메시지 설정
        if (exception instanceof OAuth2AuthenticationException) {
            String exceptionMessage = exception.getMessage();
            System.out.println("OAuth2 Authentication Exception Message: " + exceptionMessage); // 디버깅 출력

            if (exceptionMessage != null && exceptionMessage.contains("이메일 가입 회원")) {
                errorMessage = "이메일 가입 회원입니다. 구글 로그인을 이용할 수 없습니다.";
            } else {
                errorMessage = "로그인에 실패했습니다.";
            }
        }

        // 메시지 URL 인코딩
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 실패 메시지를 URL 파라미터로 전달
        response.sendRedirect("/login?oauthError=" + encodedMessage);
    }
}
