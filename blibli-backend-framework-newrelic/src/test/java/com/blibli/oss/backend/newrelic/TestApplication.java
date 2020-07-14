package com.blibli.oss.backend.newrelic;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.newrelic.configuration.EnableNewRelicReactorInstrumentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@EnableNewRelicReactorInstrumentation
public class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Bean
  public MongoProperties mongoProperties() {
    return new MongoProperties();
  }

  @RestController
  public class TestController {

    @Autowired
    private CommandExecutor commandExecutor;

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> test(@RequestParam String name) {
      return commandExecutor.execute(HelloWorldCommand.class, name)
          .map(greeting -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("data", greeting);
            return map;
          });
    }
  }

  private interface HelloWorldCommand extends Command<String, String> {}

  @Service
  public class HelloWorldCommandImpl implements HelloWorldCommand {
    @Override
    public Mono<String> execute(String requesterName) {
      return Mono.just("I say Hello World to " + requesterName);
    }
  }

}
