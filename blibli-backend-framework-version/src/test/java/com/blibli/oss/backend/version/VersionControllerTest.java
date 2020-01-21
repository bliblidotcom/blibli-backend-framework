package com.blibli.oss.backend.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = VersionControllerTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class VersionControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testVersion() {
    webTestClient.get().uri("/version")
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody().consumeWith(result -> {
      String response = new String(Objects.requireNonNull(result.getResponseBody()));
      Assertions.assertTrue(response.contains("maven.groupId="));
      Assertions.assertTrue(response.contains("maven.artifactId="));
      Assertions.assertTrue(response.contains("maven.pom.version="));
      Assertions.assertTrue(response.contains("maven.build.time="));
    });
  }

  @SpringBootApplication
  public static class Application {

  }
}
