package com.picpay.core.common.helper;

import org.springframework.stereotype.Component;

@Component
public final class LogGenerator {
  public String logMsg(final String fileName, final String message) {
    return "[LOG] " + messageBody(fileName, message);
  }

  public String errorMsg(final String fileName, final String message, final Throwable e) {
    var logMessage = "[ERRO] " + messageBody(fileName, message);

    if (e != null) {
      logMessage += " | Exception Message: " + e.getMessage();
    }

    return logMessage;
  }

  private static String messageBody(final String fileName, final String message) {
    return " Key: " + fileName + " | Message: " + message;
  }
}
