package com.picpay.core.enums;

public enum GroupsEnum {
  ZERO(0),
  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4);

  private final int number;

  GroupsEnum(int number) {
    this.number = number;
  }

  public int number() {
    return number;
  }
}
