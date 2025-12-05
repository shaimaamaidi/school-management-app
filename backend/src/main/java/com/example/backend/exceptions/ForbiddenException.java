package com.example.backend.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class ForbiddenException extends AuthenticationException {
    public ForbiddenException(String msg) {
        super(msg);
    }
}
