package com.picpay.entrypoint.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Error {
  private String uniqueId;
  private String informationCode;
  private String message;

  public Error(final String informationCode, final String message) {
    this.informationCode = informationCode;
    this.message = message;
  }
}