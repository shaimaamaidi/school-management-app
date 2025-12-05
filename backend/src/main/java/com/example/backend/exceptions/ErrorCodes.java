package com.example.backend.exceptions;

import lombok.*;


@Getter
public enum ErrorCodes {
    ADMIN_NOT_VALID(2000),
    ADMIN_ALREADY_EXISTS(2001),
    STUDIANT_NOT_VALID(3000),
    STUDIANT_ALREADY_EXISTS(3001),
    LEVEL_NOT_VALID(4000),
    ADMIN_NOT_FOUND(5000),
    STUDIANT_NOT_FOUND(6000),
    INVALID_REQUEST(7000),
    RESOURCE_ALREADY_EXISTS(8000),
    BAD_CREDENTIALS(9000),
    INTERNAL_ERROR(10000);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }

}
