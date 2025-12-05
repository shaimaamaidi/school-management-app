package com.example.backend.handler;

import com.example.backend.exceptions.*;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private HttpHeaders jsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  // 404 Not Found
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorDto> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
    ErrorDto errorDto = ErrorDto.builder()
            .code(ex.getErrorCode())
            .httpCode(HttpStatus.NOT_FOUND.value())
            .message("The requested item does not exist.")
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.NOT_FOUND);
  }

  // 400 Bad Request
  @ExceptionHandler({InvalidOperationException.class, CsvValidationException.class})
  public ResponseEntity<ErrorDto> handleBadRequest(Exception ex, WebRequest request) {
    ErrorDto errorDto = ErrorDto.builder()
            .code(ErrorCodes.INVALID_REQUEST)
            .httpCode(HttpStatus.BAD_REQUEST.value())
            .message("The request data is missing or invalid: " + ex.getMessage())
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.BAD_REQUEST);
  }

  // 401 Unauthorized
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorDto> handleUnauthorized(BadCredentialsException ex, WebRequest request) {
    ErrorDto errorDto = ErrorDto.builder()
            .code(ErrorCodes.BAD_CREDENTIALS)
            .httpCode(HttpStatus.UNAUTHORIZED.value())
            .message("The user is not authenticated or credentials are invalid.")
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.UNAUTHORIZED);
  }

  // 403 Forbidden
  @ExceptionHandler({ForbiddenException.class, UsernameNotFoundException.class, InternalAuthenticationServiceException.class})
  public ResponseEntity<ErrorDto> handleForbidden(Exception ex, WebRequest request) {
    String message;
    if (ex instanceof ForbiddenException) {
      message = "Access is forbidden: " + ex.getMessage();
    } else if (ex instanceof UsernameNotFoundException) {
      message = "Email address not found.";
    } else if (ex instanceof InternalAuthenticationServiceException) {
      message = "Authentication error. Please check your credentials.";
    } else {
      message = "Access denied.";
    }

    ErrorDto errorDto = ErrorDto.builder()
            .code(ErrorCodes.BAD_CREDENTIALS)
            .httpCode(HttpStatus.FORBIDDEN.value())
            .message(message)
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.FORBIDDEN);
  }

  // 409 Conflict
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorDto> handleConflict(ResourceAlreadyExistsException ex, WebRequest request) {
    ErrorDto errorDto = ErrorDto.builder()
            .code(ErrorCodes.RESOURCE_ALREADY_EXISTS)
            .httpCode(HttpStatus.CONFLICT.value())
            .message("Attempting to create a resource that already exists.")
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.CONFLICT);
  }

  // 500 Internal Server Error
  @ExceptionHandler({RuntimeException.class, IOException.class})
  public ResponseEntity<ErrorDto> handleInternalServerError(Exception ex, WebRequest request) {
    ErrorDto errorDto = ErrorDto.builder()
            .code(ErrorCodes.INTERNAL_ERROR)
            .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred on the server side: " + ex.getMessage())
            .build();
    return new ResponseEntity<>(errorDto, jsonHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
