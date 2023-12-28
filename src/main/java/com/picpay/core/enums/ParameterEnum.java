package com.picpay.core.enums;

public enum ParameterEnum {
  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9),
  TEN(10),
  ELEVEN(11),
  TWELVE(12),
  THIRTEEN(13),
  FOURTEEN(14),
  FIFTEEN(15),
  SIXTEEN(16),
  SEVENTEEN(17);

  private final int position;

  ParameterEnum(final int position) {
    this.position = position;
  }

  public int position() {
    return position;
  }
}
