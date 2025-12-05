package com.example.backend.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AccountNotActivatedException extends AuthenticationException {
    public AccountNotActivatedException(String msg) {
        super(msg);
    }
}
