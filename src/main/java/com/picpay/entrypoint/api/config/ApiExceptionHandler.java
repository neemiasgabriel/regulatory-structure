package com.picpay.entrypoint.api.config;

import com.picpay.entrypoint.api.dto.Error;
import com.picpay.entrypoint.api.dto.ErrorPresenter;
import com.tngtech.archunit.thirdparty.com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import com.picpay.core.exception.BadRequestException;
import com.picpay.core.exception.InternalServerException;
import com.picpay.core.exception.UnauthorizedException;
import com.picpay.core.exception.UploadException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
  private static final String BUSINESS_ERROR = "error.business.request";

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorPresenter> handleBadRequestException(final BadRequestException e) {
    return ResponseEntity.status(BAD_REQUEST).body(getError(e, BAD_REQUEST));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorPresenter> handleUnauthorizedException(final UnauthorizedException e) {
    return ResponseEntity.status(UNAUTHORIZED).body(getError(e, UNAUTHORIZED));
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorPresenter> handleNullPointerException(final NullPointerException e) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getError(e, INTERNAL_SERVER_ERROR));
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(UploadException.class)
  public ResponseEntity<ErrorPresenter> handleUploadFileRequestException(final UploadException e) {
    LocalDateTime timeStamp = LocalDateTime.now();
    final var details = new Error(BUSINESS_ERROR, "O nome do arquivo está incorreto.");
    details.setUniqueId(UUID.randomUUID().toString());
    final var presenter = ErrorPresenter.builder()
        .error("Nome do arquivo incorreto")
        .timestamp(timeStamp.toString())
        .errors(Sets.newHashSet(details))
        .transactionId(null)
        .status(BAD_REQUEST.value())
        .build();
    return ResponseEntity.status(BAD_REQUEST).body(presenter);
  }

  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<ErrorPresenter> handleInternalServerException(final InternalServerException e) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getError(e, INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorPresenter> handleException(final Throwable exception, final HttpServletRequest request) {
    final var status = HttpStatus.INTERNAL_SERVER_ERROR;
    final var path = request.getRequestURI();
    final var errorUniqueId = UUID.randomUUID().toString();
    final var apiError = new Error(errorUniqueId, status.toString(), "Serviço indisponível");
    final var presenter = new ErrorPresenter(apiError, status, path);
    presenter.setError(exception.getMessage());
    return new ResponseEntity<>(presenter, new HttpHeaders(), status);
  }

  private static ErrorPresenter getError(final Throwable t, final HttpStatus status) {
    final var errors = getErrorDetails(t);
    return ErrorPresenter.builder()
        .errors(errors)
        .status(status.value())
        .build();
  }

  private static Set<Error> getErrorDetails(final Throwable e) {
    final var details = new Error();
    details.setInformationCode(BUSINESS_ERROR);
    details.setMessage(e.getMessage());

    return Sets.newHashSet(details);
  }
}
