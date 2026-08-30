package com.airline.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (uri.startsWith("/admin")) {
            if (session == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
                response.sendRedirect("/login?error=Admin+login+required+to+access+this+page.");
                return false;
            }
        }

        if (uri.startsWith("/customer")) {
            if (session == null || session.getAttribute("customer") == null) {
                response.sendRedirect("/login?error=Please+login+to+access+customer+portal.");
                return false;
            }
        }

        return true;
    }
}
