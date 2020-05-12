package com.blibli.oss.backend.externalapi.exception;

public class ExternalApiException extends RuntimeException {

  public ExternalApiException() {
  }

  public ExternalApiException(String message) {
    super(message);
  }
}
