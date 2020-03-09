package com.blibli.oss.backend.validation;

import reactor.core.publisher.Mono;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public interface ReactiveConstraintValidator<A extends Annotation, T> extends ConstraintValidator<A, T> {

  default boolean isValid(T value, ConstraintValidatorContext context) {
    return validate(value, context).block();
  }

  Mono<Boolean> validate(T value, ConstraintValidatorContext context);

}
