package com.blibli.oss.backend.common.model;

import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.common.model.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = ResponseTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ResponseTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testPaging() {
    webTestClient.get()
      .uri("/paging")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("data[*]").value(Matchers.contains("Eko", "Kurniawan", "Khannedy"))
      .jsonPath("paging.page").isEqualTo(1)
      .jsonPath("paging.item_per_page").isEqualTo(3)
      .jsonPath("paging.total_item").isEqualTo(30)
      .jsonPath("paging.total_page").isEqualTo(10)
      .jsonPath("paging.sort_by[0].property_name").isEqualTo("first_name")
      .jsonPath("paging.sort_by[0].direction").isEqualTo("asc");
  }

  @Test
  void testHello() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/hello").queryParam("name", "Eko").build())
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("status").isEqualTo(HttpStatus.OK.name())
      .jsonPath("data.hello").isEqualTo("Hello Eko");
  }

  @Test
  void testBadRequest() {
    webTestClient.get()
      .uri("/bad-request")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.BAD_REQUEST.value())
      .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.name())
      .jsonPath("errors.first_name").value(Matchers.contains("NotBlank", "NotNull"));
  }

  @Test
  void testInternalServerError() {
    webTestClient.get()
      .uri("/internal-server-error")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .jsonPath("status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.name());
  }

  @Test
  void testOk() {
    webTestClient.get()
      .uri("/ok")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.OK.value())
      .jsonPath("status").isEqualTo(HttpStatus.OK.name());
  }

  @Test
  void testUnauthorized() {
    webTestClient.get()
      .uri("/unauthorized")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
      .jsonPath("status").isEqualTo(HttpStatus.UNAUTHORIZED.name());
  }

  @Test
  void testRedirect() {
    webTestClient.get()
      .uri("/redirect")
      .exchange()
      .expectBody()
      .jsonPath("code").isEqualTo(HttpStatus.PERMANENT_REDIRECT.value())
      .jsonPath("status").isEqualTo(HttpStatus.PERMANENT_REDIRECT.name());
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ExampleController {

      @GetMapping(value = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<List<String>>> paging() {
        return Mono.just(ResponseHelper.ok(
          Arrays.asList("Eko", "Kurniawan", "Khannedy"),
          Paging.builder()
            .page(1)
            .itemPerPage(3)
            .totalItem(30)
            .totalPage(10)
            .sortBy(Arrays.asList(
              SortBy.builder()
                .propertyName("first_name")
                .direction("asc")
                .build()
            ))
            .build()
        ));
      }

      @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Response<SayHelloResponse>> response(@RequestParam("name") String name) {
        return Mono.just(
          ResponseHelper.ok(
            SayHelloResponse.builder().hello(String.format("Hello %s", name)).build()
          )
        );
      }

      @GetMapping("/bad-request")
      public Mono<Response<String>> badRequest() {
        return Mono.just(ResponseHelper.badRequest(
          Collections.singletonMap("first_name", Arrays.asList("NotBlank", "NotNull"))
        ));
      }

      @GetMapping("/internal-server-error")
      public Mono<Response<String>> internalServerError() {
        return Mono.just(ResponseHelper.internalServerError());
      }

      @GetMapping("/ok")
      public Mono<Response<String>> ok() {
        return Mono.just(ResponseHelper.ok());
      }

      @GetMapping("/unauthorized")
      public Mono<Response<String>> unauthorized() {
        return Mono.just(ResponseHelper.unauthorized());
      }

      @GetMapping("/redirect")
      public Mono<Response<String>> redirect() {
        return Mono.just(ResponseHelper.status(HttpStatus.PERMANENT_REDIRECT));
      }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SayHelloResponse {

      private String hello;
    }

  }
}
