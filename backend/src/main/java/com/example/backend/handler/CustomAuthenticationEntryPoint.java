package com.example.backend.handler;

import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.exceptions.ForbiddenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        int status = (authException instanceof ForbiddenException) ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED;

        ErrorDto errorDto = ErrorDto.builder()
                .code(ErrorCodes.BAD_CREDENTIALS)
                .httpCode(status)
                .message(authException.getMessage())
                .build();

        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorDto));
    }
}
