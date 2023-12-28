package com.picpay.core.enums;

import java.util.Arrays;

public enum CorpEnum {
  SICRED("Sicred"),
  FIDELITY("Fidelity");

  private final String corp;

  CorpEnum(final String corp) {
    this.corp = corp;
  }

  public String corp() {
    return corp;
  }

  public static CorpEnum getCorpEnum(final String corpName) {
    return Arrays.stream(CorpEnum.values()).filter(corpEnum -> corpEnum.corp.equalsIgnoreCase(corpName))
        .findFirst()
        .orElse(null);
  }
}
