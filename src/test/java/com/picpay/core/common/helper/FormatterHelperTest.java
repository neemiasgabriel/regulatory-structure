package com.picpay.core.common.helper;

import com.picpay.mocks.MockFormatterData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatterHelperTest {
  private FormatterHelper helper;

  @BeforeEach
  public void setup() {
    helper = new FormatterHelper();
  }

  @Test
  void whenReceiveValueThenReturnFixedFormattedValue() {
    final var expected = "218799.18";
    final var result = helper.fixMoneyString(MockFormatterData.UNFORMATTED_VALUE);

    Assertions.assertEquals(expected, result);
  }

  @Test
  void whenReceiveDateThenConvertToYearMonthDayWithDash() {
    final var expected = "2023-09-01";
    final var result = helper.toYearMonthDayWithDash(MockFormatterData.UNFORMATTED_DATE);

    Assertions.assertEquals(expected, result);
  }
}
