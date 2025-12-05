package com.example.backend.exceptions;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private ErrorCodes errorCode;
    public ResourceAlreadyExistsException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

