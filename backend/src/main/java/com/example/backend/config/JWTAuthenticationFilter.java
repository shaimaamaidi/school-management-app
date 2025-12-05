package com.example.backend.config;

import com.example.backend.handler.CustomAuthenticationEntryPoint;
import com.example.backend.repository.TokenRepository;
import com.example.backend.service.impl.Authentication.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader= request.getHeader("Authorization");
        final String requestPath=request.getRequestURI();
        try {
            if ((requestPath.startsWith("/api/auth/") || requestPath.startsWith("/ws") || requestPath.startsWith("/v2/api-docs") || requestPath.startsWith("/v3/api-docs") || requestPath.startsWith("/swagger-resources") || requestPath.startsWith("/configuration") || requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/webjars") || requestPath.equals("/swagger-ui.html"))) {
                filterChain.doFilter(request,response);
                return;
            }

            if(authHeader==null){
                throw new BadCredentialsException("Le token semble est manquant.");
            }

            if(!authHeader.startsWith("Bearer ")){
                throw new BadCredentialsException("Le token semble incorrecte.");
            }

            final String jwt = authHeader.substring(7);
            final String userMail;
            try {
                userMail = jwtService.extractUserName(jwt);
            } catch (Exception e) {
                throw new BadCredentialsException("Token JWT est invalide ou corrompu.");
            }
            //user not authenticated
            if (userMail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userMail);

                boolean isTokenValid = tokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);
                //check if the user and token is valid or not
                if (jwtService.isValidToken(jwt, userDetails) && isTokenValid && jwtService.isAccessToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            authenticationEntryPoint.commence(request, response, ex);
        }
    }
}
