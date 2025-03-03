package com.capstone.employeeSystem.security;

import com.capstone.employeeSystem.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            logger.info("🔐 Extracted Token: {}", token);

            String username = jwtUtil.extractUsername(token);
            logger.info("👤 Extracted Username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    logger.info("✅ Token is valid for user: {}", username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("❌ Token validation failed for user: {}", username);
                }
            }
        } else {
            logger.warn("⚠️ No Authorization header found or doesn't start with 'Bearer '");
        }

        filterChain.doFilter(request, response);
    }
}
