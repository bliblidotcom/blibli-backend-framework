package com.blibli.oss.backend.command.controller;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.response.Response;
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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CommandErrorControllerTest.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommandErrorControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testValidationError() {
    webTestClient.post().uri("/hello")
      .body(BodyInserters.fromValue(HelloCommandRequest.builder().name("").build()))
      .exchange()
      .expectStatus().is4xxClientError()
      .expectBody()
      .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
      .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.name())
      .jsonPath("$.errors.name").isEqualTo("NotBlank");
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public HelloCommand helloCommand() {
      return new HelloCommandImpl();
    }

    @Slf4j
    @RestControllerAdvice
    public static class ErrorController implements MessageSourceAware, CommandErrorController {

      @Getter
      @Setter
      private MessageSource messageSource;

      @Override
      public Logger getLogger() {
        return log;
      }
    }

    @RestController
    public static class HelloController {

      @Autowired
      private CommandExecutor commandExecutor;

      @PostMapping(value = "/hello", consumes = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<String>> hello(@RequestBody HelloCommandRequest request) {
        return commandExecutor.execute(HelloCommand.class, request)
          .map(ResponseHelper::ok);
      }

    }

  }

  public interface HelloCommand extends Command<HelloCommandRequest, String> {

  }

  public static class HelloCommandImpl implements HelloCommand {

    @Override
    public Mono<String> execute(HelloCommandRequest request) {
      return Mono.just(String.format("Hello %s", request));
    }
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HelloCommandRequest {

    @NotBlank(message = "NotBlank")
    private String name;

  }

}
