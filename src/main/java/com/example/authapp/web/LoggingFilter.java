package com.example.authapp.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("keyce-audit");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "anonymous";
        String role = (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty())
                ? auth.getAuthorities().iterator().next().getAuthority()
                : "NONE";

        String method = request.getMethod();
        String path = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("{} {} -> status={} user={} role={}", method, path, response.getStatus(), username, role);
        }
    }
}

