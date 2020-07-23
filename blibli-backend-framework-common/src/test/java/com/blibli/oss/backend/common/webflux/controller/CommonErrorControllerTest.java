package com.blibli.oss.backend.common.webflux.controller;

import com.blibli.oss.backend.common.annotation.MetaData;
import com.blibli.oss.backend.common.annotation.MetaDatas;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = CommonErrorControllerTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CommonErrorControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testThrowable() {
    webTestClient.get()
      .uri("/Throwable")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testConstraintViolationException() {
    webTestClient.post()
      .uri("/ConstraintViolationException")
      .body(BodyInserters.fromValue(Application.HelloRequest.builder().build()))
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testWebExchangeBindException() {
    webTestClient.post()
      .uri("/WebExchangeBindException")
      .body(BodyInserters.fromValue(Application.HelloRequest.builder().build()))
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testHttpMessageNotReadableException() {
    webTestClient.get()
      .uri("/HttpMessageNotReadableException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testServerWebInputException() {
    webTestClient.get()
      .uri("/ServerWebInputException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testResponseStatusException() {
    webTestClient.get()
      .uri("/ResponseStatusException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
      .expectBody()
      .jsonPath("$.errors.reason").isEqualTo("Ups");
  }

  @Test
  void testMediaTypeNotSupportedStatusException() {
    webTestClient.get()
      .uri("/MediaTypeNotSupportedStatusException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Test
  void testNotAcceptableStatusException() {
    webTestClient.get()
      .uri("/NotAcceptableStatusException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
  }

  @Test
  void testUnsupportedMediaTypeStatusException() {
    webTestClient.get()
      .uri("/UnsupportedMediaTypeStatusException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Test
  void testMethodNotAllowedException() {
    webTestClient.post()
      .uri("/MethodNotAllowedException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Test
  void testServerErrorException() {
    webTestClient.get()
      .uri("/ServerErrorException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testMetadataException() {
    webTestClient.get()
      .uri("/MetadataException")
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
      .expectBody()
      .jsonPath("$.errors.name").isEqualTo("NotBlank")
      .jsonPath("$.errors.age").isEqualTo("NotNull")
      .jsonPath("$.errors['nested.name']").isEqualTo("NotBlank")
      .jsonPath("$.errors['nested.age']").isEqualTo("NotNull")
      .jsonPath("$.metadata.errors['name'].message").isEqualTo("NotBlank")
      .jsonPath("$.metadata.errors['age'].message").isEqualTo("NotNull")
      .jsonPath("$.metadata.errors['nested.name'].message").isEqualTo("NotBlank")
      .jsonPath("$.metadata.errors['nested.age'].message").isEqualTo("NotNull");
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ExampleController {

      @Autowired
      private ExampleService exampleService;

      @Autowired
      private Validator validator;

      @GetMapping("/Throwable")
      public String throwable() throws Throwable {
        throw new Throwable("Internal Server Error");
      }

      @PostMapping(value = "/ConstraintViolationException", consumes = MediaType.APPLICATION_JSON_VALUE)
      public String constraintViolationException(@RequestBody HelloRequest request) {
        exampleService.validated(request);
        return "OK";
      }

      @PostMapping(value = "/WebExchangeBindException", consumes = MediaType.APPLICATION_JSON_VALUE)
      public String webExchangeBindException(@RequestBody @Validated HelloRequest request) {
        return "OK";
      }

      @GetMapping("/HttpMessageNotReadableException")
      public String httpMessageNotReadableException() {
        throw new HttpMessageNotReadableException(null, (HttpInputMessage) null);
      }

      @GetMapping("/ServerWebInputException")
      public String serverWebInputException() throws NoSuchMethodException {
        MethodParameter methodParameter = MethodParameter.forParameter(ExampleService.class.getMethod("validated", HelloRequest.class).getParameters()[0]);
        methodParameter.initParameterNameDiscovery(new StandardReflectionParameterNameDiscoverer());
        throw new ServerWebInputException("Ups", methodParameter);
      }

      @GetMapping("/ResponseStatusException")
      public String responseStatusException() {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ups");
      }

      @GetMapping(value = "/MediaTypeNotSupportedStatusException")
      public String mediaTypeNotSupportedStatusException() {
        throw new MediaTypeNotSupportedStatusException("Ups");
      }

      @GetMapping(value = "/NotAcceptableStatusException")
      public String notAcceptableStatusException() {
        throw new NotAcceptableStatusException("Ups");
      }

      @GetMapping("/UnsupportedMediaTypeStatusException")
      public String unsupportedMediaTypeStatusException() {
        throw new UnsupportedMediaTypeStatusException("Ups");
      }

      @GetMapping("/MethodNotAllowedException")
      public String methodNotAllowedException() {
        return "OK";
      }

      @GetMapping("/ServerErrorException")
      public String serverErrorException() {
        throw new ServerErrorException("Ups", new NullPointerException());
      }

      @GetMapping("/MetadataException")
      public String metadataValidation(){
        SampleRequest sampleRequest = SampleRequest.builder()
          .name("")
          .age(null)
          .nested(NestedSampleRequest.builder()
            .name("")
            .age(null)
            .build())
          .build();

        Set<ConstraintViolation<SampleRequest>> constraintViolations = validator.validate(sampleRequest);
        throw new ConstraintViolationException(constraintViolations);
      }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SampleRequest {

      @MetaDatas(
        @MetaData(key = "message", value = "NotBlank")
      )
      @NotBlank(message = "NotBlank")
      private String name;

      @MetaDatas(
        @MetaData(key = "message", value = "NotNull")
      )
      @NotNull(message = "NotNull")
      private Integer age;

      @Valid
      private NestedSampleRequest nested;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NestedSampleRequest {

      @MetaDatas(
        @MetaData(key = "message", value = "NotBlank")
      )
      @NotBlank(message = "NotBlank")
      private String name;

      @MetaDatas(
        @MetaData(key = "message", value = "NotNull")
      )
      @NotNull(message = "NotNull")
      private Integer age;

    }

    @Service
    @Validated
    public static class ExampleService {

      @Validated
      public void validated(@Valid HelloRequest request) {

      }

    }

    @Slf4j
    @RestControllerAdvice
    public static class ErrorController implements CommonErrorController, MessageSourceAware {

      @Getter
      @Setter
      private MessageSource messageSource;

      @Override
      public Logger getLogger() {
        return log;
      }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HelloRequest {

      @NotBlank
      private String name;
    }

  }
}
