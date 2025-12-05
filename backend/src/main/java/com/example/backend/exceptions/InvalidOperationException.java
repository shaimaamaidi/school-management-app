package com.example.backend.exceptions;

import lombok.Getter;

@Getter
public class InvalidOperationException extends RuntimeException {

  private ErrorCodes errorCode;

  public InvalidOperationException(String message, ErrorCodes errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}
