package com.blibli.oss.backend.common.webflux.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
  void test() {
    webTestClient.post()
      .uri("/MethodArgumentNotValidException")
      .body(BodyInserters.fromValue(Application.HelloRequest.builder().build()))
      .exchange()
      .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @SpringBootApplication
  public static class Application {

    @Service
    @Validated
    public static class ExampleService {

      @Validated
      public void validated(@Valid HelloRequest request){

      }

    }

    @RestController
    public static class ExampleController {

      @Autowired
      private ExampleService exampleService;

      @GetMapping("/Throwable")
      public String throwable() throws Throwable {
        throw new Throwable("Internal Server Error");
      }

      @PostMapping(value = "/MethodArgumentNotValidException", consumes = MediaType.APPLICATION_JSON_VALUE)
      public String methodArgumentNotValidException(@RequestBody HelloRequest request) {
        exampleService.validated(request);
        return "OK";
      }

      @GetMapping("/BindException")
      public String bindException() {
        return "OK";
      }

      @GetMapping("/HttpMessageNotReadableException")
      public String httpMessageNotReadableException() {
        return "OK";
      }

      @GetMapping("/ServerWebInputException")
      public String serverWebInputException() {
        return "OK";
      }

      @GetMapping("/WebExchangeBindException")
      public String webExchangeBindException() {
        return "OK";
      }

      @GetMapping("/ResponseStatusException")
      public String responseStatusException() {
        return "OK";
      }

      @GetMapping("/MediaTypeNotSupportedStatusException")
      public String mediaTypeNotSupportedStatusException() {
        return "OK";
      }

      @GetMapping("/NotAcceptableStatusException")
      public String notAcceptableStatusException() {
        return "OK";
      }

      @GetMapping("/UnsupportedMediaTypeStatusException")
      public String unsupportedMediaTypeStatusException() {
        return "OK";
      }

      @GetMapping("/MethodNotAllowedException")
      public String methodNotAllowedException() {
        return "OK";
      }

      @GetMapping("/ServerErrorException")
      public String serverErrorException() {
        return "OK";
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
