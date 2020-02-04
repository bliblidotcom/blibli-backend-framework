package com.blibli.oss.backend.command.cache;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.executor.CommandExecutor;
import lombok.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CommandCacheInterceptorTest.Application.class)
public class CommandCacheInterceptorTest {

  @Autowired
  private CommandExecutor commandExecutor;

  private RedisServer redisServer;

  @Autowired
  private HelloCommandImpl helloCommand;

  @BeforeEach
  void setUp() throws IOException {
    redisServer = new RedisServer(6379);
    redisServer.start();
  }

  @AfterEach
  void tearDown() {
    redisServer.stop();
  }

  @Test
  void testExecuteSuccess() {
    StepVerifier.create(commandExecutor.execute(MyCommand.class, "Eko"))
      .expectNext("Hello Eko")
      .verifyComplete();
  }

  @Test
  void testExecuteCacheSuccess() {
    StepVerifier.create(commandExecutor.execute(HelloCommand.class, HelloCommandRequest.builder().name("Eko").build()))
      .expectNext(HelloCommandResponse.builder().response("Hello Eko").build())
      .verifyComplete();

    Assertions.assertEquals(helloCommand.getCounter(), 1);

    StepVerifier.create(commandExecutor.execute(HelloCommand.class, HelloCommandRequest.builder().name("Eko").build()))
      .expectNext(HelloCommandResponse.builder().response("Hello Eko").build())
      .verifyComplete();

    StepVerifier.create(commandExecutor.execute(HelloCommand.class, HelloCommandRequest.builder().name("Eko").build()))
      .expectNext(HelloCommandResponse.builder().response("Hello Eko").build())
      .verifyComplete();

    Assertions.assertEquals(helloCommand.getCounter(), 1);
  }

  @SpringBootApplication
  @PropertySource("classpath:cache.properties")
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

    @Getter
    private int counter = 0;

    @Override
    public Mono<HelloCommandResponse> execute(HelloCommandRequest request) {
      return Mono.just(HelloCommandResponse.builder().response(String.format("Hello %s", request.getName())).build())
        .doOnNext(helloCommandResponse -> counter++);
    }

    @Override
    public String cacheKey(HelloCommandRequest request) {
      return request.getName();
    }

    @Override
    public Class<HelloCommandResponse> responseClass() {
      return HelloCommandResponse.class;
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
