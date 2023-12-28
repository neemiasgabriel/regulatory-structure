package com.picpay.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public abstract class GeneralException extends RuntimeException {
  private final HttpStatus statusCode;
  private final String transactionId;
  private final RuntimeException ex;

  protected GeneralException(String message, HttpStatus statusCode) {
    super(message);
    this.statusCode = statusCode;
    this.transactionId = null;
    this.ex = null;
  }

  protected GeneralException(String message, HttpStatus statusCode, RuntimeException ex) {
    super(message);
    this.statusCode = statusCode;
    this.transactionId = null;
    this.ex = ex;
  }

}
