package com.picpay.core.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateValidatorTest {
  private DateValidator validator;

  @BeforeAll
  void setup() {
    validator = new DateValidator();
  }

  @Test
  void whenIsMonthDayYearThenReturnTrue() {
    final var result = validator.isMonthDayYear("08/23/2023");
    Assertions.assertTrue(result);
  }

  @Test
  void whenIsNotMonthDayYearThenReturnFalse() {
    final var result = validator.isMonthDayYear("13/23/2023");
    Assertions.assertFalse(result);
  }

  @Test
  void whenIsDayMonthYearThenReturnTrue() {
    final var result = validator.isDayMonthYear("23082023");
    Assertions.assertTrue(result);
  }

  @Test
  void whenIsNotDayMonthYearThenReturnFalse() {
    final var result = validator.isDayMonthYear("32082023");
    Assertions.assertFalse(result);
  }

  @Test
  void whenIsTimestampThenReturnTrue() {
    final var result = validator.isTimestamp("20230823080130");
    Assertions.assertTrue(result);
  }

  @Test
  void whenIsNotTimestampThenReturnFalse() {
    final var result = validator.isTimestamp("20230823080161");
    Assertions.assertFalse(result);
  }
}