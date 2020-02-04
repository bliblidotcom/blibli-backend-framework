package com.blibli.oss.backend.command.executor;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.exception.CommandValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.validation.constraints.NotBlank;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DefaultCommandExecutorTest.Application.class)
public class DefaultCommandExecutorTest {

  @Autowired
  private CommandExecutor commandExecutor;

  @Test
  void testExecuteSuccess() {
    StepVerifier.create(commandExecutor.execute(MyCommand.class, "Eko"))
      .expectNext("Hello Eko")
      .verifyComplete();
  }

  @Test
  void testExecuteObjectSuccess() {
    StepVerifier.create(commandExecutor.execute(HelloCommand.class, HelloCommandRequest.builder().name("Eko").build()))
      .expectNext(HelloCommandResponse.builder().response("Hello Eko").build())
      .verifyComplete();
  }

  @Test
  void testValidationError() {
    StepVerifier.create(commandExecutor.execute(HelloCommand.class, HelloCommandRequest.builder().name("").build()))
      .expectError(CommandValidationException.class)
      .verify();
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public MyCommand myCommand() {
      return new MyCommandImpl();
    }

    @Bean
    public HelloCommand helloCommand() {
      return new HelloCommandImpl();
    }

  }

  public interface MyCommand extends Command<String, String> {

  }

  public static class MyCommandImpl implements MyCommand {

    @Override
    public Mono<String> execute(String request) {
      return Mono.just(String.format("Hello %s", request));
    }
  }

  public interface HelloCommand extends Command<HelloCommandRequest, HelloCommandResponse> {

  }

  public static class HelloCommandImpl implements HelloCommand {

    @Override
    public Mono<HelloCommandResponse> execute(HelloCommandRequest request) {
      return Mono.just(HelloCommandResponse.builder().response(String.format("Hello %s", request.getName())).build());
    }
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  private static class HelloCommandRequest {

    @NotBlank
    private String name;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  private static class HelloCommandResponse {

    private String response;
  }
}
