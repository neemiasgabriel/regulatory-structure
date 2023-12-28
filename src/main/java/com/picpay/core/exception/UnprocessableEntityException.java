package com.picpay.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends GeneralException {

  public UnprocessableEntityException(String message, HttpStatus statusCode) {
    super(message, statusCode);
  }
}
