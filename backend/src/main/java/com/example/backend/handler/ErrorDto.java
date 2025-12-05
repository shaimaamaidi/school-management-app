package com.example.backend.handler;

import com.example.backend.exceptions.ErrorCodes;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ErrorDto {

  private Integer httpCode;

  private ErrorCodes code;

  private String message;

}
