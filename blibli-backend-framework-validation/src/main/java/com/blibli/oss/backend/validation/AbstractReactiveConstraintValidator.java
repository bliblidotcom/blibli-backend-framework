package com.blibli.oss.backend.validation;

import reactor.core.publisher.Mono;

import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public abstract class AbstractReactiveConstraintValidator<A extends Annotation, T> implements ReactiveConstraintValidator<A, T> {

  private A annotation;

  @Override
  public void initialize(A constraintAnnotation) {
    this.annotation = constraintAnnotation;
  }

  @Override
  public Mono<Boolean> validate(T value, ConstraintValidatorContext context) {
    return validate(value, annotation, context);
  }

  public abstract Mono<Boolean> validate(T value, A annotation, ConstraintValidatorContext context);
}
