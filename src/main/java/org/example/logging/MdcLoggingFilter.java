package org.example.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        // In a real app, this would come from auth; for demo we fake it
        String userId = "demo-user";

        try {
            MDC.put("requestId", requestId);
            MDC.put("path", path);
            MDC.put("method", method);
            MDC.put("clientIp", clientIp);
            MDC.put("userId", userId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
