package com.picpay.entrypoint.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ErrorPresenter {
  private String timestamp;
  private Integer status;
  private String error;
  private String path;
  private String transactionId;
  private Set<Error> errors = new HashSet<>();

  public ErrorPresenter(Error apiError, HttpStatus status, String path) {
    errors.add(apiError);
    this.status = status.value();
    this.path = path;
  }
}