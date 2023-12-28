package com.picpay.core.common.helper;


import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class FormatterHelper {
  private static final Pattern REMOVE_LEADING_ZEROS = Pattern.compile("^0+(?!$)");
  private static final Pattern pattern = Pattern.compile("[^a-zA-Z0-9_ \\n/,]");

  public String fixMoneyString(final String value) {
    return value.trim()
        .replace(",", ".")
        .replaceAll(REMOVE_LEADING_ZEROS.pattern(), "");
  }

  public String toYearMonthDayWithDash(final String dateStr) {
    final var day = dateStr.substring(3, 5);
    final var month = dateStr.substring(0, 2);
    final var year = dateStr.substring(6);

    return year + "-" + month + "-" + day;
  }

  public String fixNonAsciiCharacters(final String data) {
    return pattern.matcher(data).replaceAll(" ");
  }
}
