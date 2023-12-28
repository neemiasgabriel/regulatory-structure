package com.picpay.entrypoint.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ErrorMessage {
  private String message;
  private String code;
  private ErrorDetail[] errors;
}
