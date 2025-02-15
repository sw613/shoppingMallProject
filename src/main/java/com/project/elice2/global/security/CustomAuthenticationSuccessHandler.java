package com.project.elice2.global.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("AuthenticationSuccessHandler - Login successful for user: " + authentication.getName());

        SavedRequest savedRequest = (SavedRequest) request.getSession()
                .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            System.out.println("AuthenticationSuccessHandler - Redirecting to saved request: " + targetUrl);
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            System.out.println("AuthenticationSuccessHandler - No saved request, redirecting to default URL");
            redirectStrategy.sendRedirect(request, response, "/product/home");
        }
    }

}
