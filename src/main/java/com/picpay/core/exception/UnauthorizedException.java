package com.picpay.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends GeneralException {
  public UnauthorizedException(String message, HttpStatus statusCode) {
    super(message, statusCode);
  }

}

