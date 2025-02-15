package com.project.elice2.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        System.out.println("CustomRequestCache - Attempting to save request: " + requestURI);

        if (requestURI.matches("^/(assets|img|css|js|files)/.*")) {
            System.out.println("CustomRequestCache - Ignored static resource: " + requestURI);
            return;
        }

        super.saveRequest(request, response);
        System.out.println("CustomRequestCache - Request saved successfully: " + requestURI);
    }
}