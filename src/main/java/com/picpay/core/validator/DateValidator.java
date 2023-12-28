package com.picpay.core.validator;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class DateValidator {
  public boolean isMonthDayYear(final String dateStr) {
    final var sdf = new SimpleDateFormat("MM/dd/yyyy");
    sdf.setLenient(false);

    return parseResult(sdf, dateStr);
  }

  public boolean isDayMonthYear(final String dateStr) {
    final var sdf = new SimpleDateFormat("ddMMyyyy");
    sdf.setLenient(false);

    return parseResult(sdf, dateStr);
  }

  public boolean isTimestamp(final String dateStr) {
    final var sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    sdf.setLenient(false);

    return parseResult(sdf, dateStr);
  }

  private static boolean parseResult(final SimpleDateFormat sdf, final String dateStr) {
    try {
      sdf.parse(dateStr);
    } catch (ParseException e) {
      return false;
    }

    return true;
  }
}
