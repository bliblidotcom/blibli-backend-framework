package com.blibli.oss.backend.common.webflux.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = CommonErrorControllerTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CommonErrorControllerTest {

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ExampleController {

      @GetMapping("/Throwable")
      public String throwable() throws Throwable {
        throw new Throwable("Internal Server Error");
      }

      @GetMapping("/MethodArgumentNotValidException")
      public String methodArgumentNotValidException() {
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

  }
}
