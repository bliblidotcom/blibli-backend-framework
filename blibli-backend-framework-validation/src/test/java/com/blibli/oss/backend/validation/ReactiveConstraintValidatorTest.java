package com.blibli.oss.backend.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import javax.validation.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReactiveConstraintValidatorTest.Application.class)
class ReactiveConstraintValidatorTest {

  @Autowired
  private Validator validator;

  @Test
  void testValid() {
    HelloRequest helloRequest = HelloRequest.builder()
      .name("EKO")
      .build();

    Set<ConstraintViolation<HelloRequest>> constraintViolations = validator.validate(helloRequest);
    assertTrue(constraintViolations.isEmpty());
  }

  @Test
  void testInvalid() {
    HelloRequest helloRequest = HelloRequest.builder()
      .name("WRONG")
      .build();

    Set<ConstraintViolation<HelloRequest>> constraintViolations = validator.validate(helloRequest);
    assertFalse(constraintViolations.isEmpty());
  }

  @SpringBootApplication
  static class Application {

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static class HelloRequest {

    @MustValid(message = "MustValid")
    private String name;

  }

  @Documented
  @Constraint(validatedBy = {MustValidValidator.class})
  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @interface MustValid {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

  }

  static class MustValidValidator extends AbstractReactiveConstraintValidator<MustValid, String> {

    @Override
    public Mono<Boolean> validate(String value, MustValid annotation, ConstraintValidatorContext context) {
      return Mono.fromCallable(() -> "EKO".equals(value));
    }
  }

}