package com.blibli.oss.backend.command.exception;

public class CommandRuntimeException extends RuntimeException {

  public CommandRuntimeException() {
  }

  public CommandRuntimeException(Throwable cause) {
    super(cause);
  }

  public CommandRuntimeException(String message) {
    super(message);
  }
}
