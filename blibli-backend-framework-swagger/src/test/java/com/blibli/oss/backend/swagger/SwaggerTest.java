package com.blibli.oss.backend.swagger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = SwaggerTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SwaggerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testSwaggerUi() {
    webTestClient.get().uri("/swagger-ui.html")
      .exchange()
      .expectStatus().is3xxRedirection();
  }

  @Test
  void testApiDocs() {
    webTestClient.get().uri("/v3/api-docs")
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.info.title").isEqualTo("Eko Kurniawan")
      .jsonPath("$.info.version").isEqualTo("1.0.0");
  }

  @Test
  void testController() {
    webTestClient.get().uri("/v3/api-docs")
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.paths./").isNotEmpty()
      .jsonPath("$.paths./hello").isNotEmpty()
      .jsonPath("$.paths./example").isNotEmpty();
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class HomeController {

      @GetMapping("/")
      public Mono<String> home() {
        return Mono.just("Home");
      }

      @GetMapping(value = "/example", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<ExampleResponse> example() {
        return Mono.just(ExampleResponse.builder().hello("Hello").build());
      }

      @PostMapping(value = "/hello", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<ExampleResponse> hello(@RequestBody ExampleRequest request) {
        return Mono.just(ExampleResponse.builder().hello("Hello " + request.getName()).build());
      }

      @Data
      @Builder
      @AllArgsConstructor
      @NoArgsConstructor
      public static class ExampleResponse {

        private String hello;

      }

      @Data
      @Builder
      @AllArgsConstructor
      @NoArgsConstructor
      public static class ExampleRequest {

        private String name;
      }

    }

  }
}
