package com.eCommerceSite.WhimsyCrafts.security;

import com.eCommerceSite.WhimsyCrafts.services.CustomerDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        if ("/WhimsyCrafts/addLogo".equals(request.getServletPath()) && request instanceof MultipartHttpServletRequest) {
                filterChain.doFilter(request, response);
                return;
            }
        try {
            String token = jwtTokenProvider.resolveToken(request);
            UserDetails userDetails = jwtTokenProvider.getUserDetailsFromToken(token);
            if(token != null) {
                jwtTokenProvider.validateToken(token, userDetails);
                String email = jwtTokenProvider.extractUsername(token);
                setAuthentication(token, email, request);
            }

        } catch (Exception e) {
            logger.error("Invalid JWT signature");
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, String userId, HttpServletRequest httpServletRequest) {
        if (userId != null) {
            UserDetails userDetails = customerDetailsService.loadUserByUsername(userId);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenProvider.getAuthentication(token, userDetails);
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }
}