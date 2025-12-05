package com.example.backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class ForbiddenException extends AuthenticationException {
    public ForbiddenException(String msg) {
        super(msg);
    }
}
