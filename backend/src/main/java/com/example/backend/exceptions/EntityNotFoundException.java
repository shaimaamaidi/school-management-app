package com.example.backend.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

  private ErrorCodes errorCode;

  public EntityNotFoundException(String message, ErrorCodes errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

}
