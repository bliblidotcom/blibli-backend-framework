package com.blibli.oss.backend.common.model;

import com.blibli.oss.backend.common.model.response.Response;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = ResponseTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ResponseTest {

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ExampleController {


    }

    public static class

  }
}
