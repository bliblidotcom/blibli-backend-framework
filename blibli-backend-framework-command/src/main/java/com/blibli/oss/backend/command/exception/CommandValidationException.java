package com.blibli.oss.backend.command.exception;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class CommandValidationException extends CommandRuntimeException {

  @Getter
  private Set<ConstraintViolation<?>> constraintViolations;

  public CommandValidationException(Set constraintViolations) {
    this(null, constraintViolations);
  }

  public CommandValidationException(String message, Set constraintViolations) {
    super(message);
    this.constraintViolations = constraintViolations;
  }
}
