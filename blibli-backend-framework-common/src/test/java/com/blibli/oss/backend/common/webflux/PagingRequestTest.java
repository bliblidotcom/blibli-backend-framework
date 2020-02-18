package com.blibli.oss.backend.common.webflux;

import com.blibli.oss.backend.common.helper.PagingHelper;
import com.blibli.oss.backend.common.model.request.PagingRequest;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.common.properties.PagingProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = PagingRequestTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class PagingRequestTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private PagingProperties pagingProperties;

  @Test
  void testPagingRequest() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/paging-request")
        .queryParam(pagingProperties.getQuery().getPageKey(), 1)
        .queryParam(pagingProperties.getQuery().getItemPerPageKey(), 100)
        .queryParam(pagingProperties.getQuery().getSortByKey(), "first_name:ASC,last_name:DESC")
        .build()
      )
      .exchange()
      .expectBody()
      .jsonPath("$.page").isEqualTo(1)
      .jsonPath("$.item_per_page").isEqualTo(100)
      .jsonPath("$.sort_by[0].property_name").isEqualTo("first_name")
      .jsonPath("$.sort_by[0].direction").isEqualTo("ASC")
      .jsonPath("$.sort_by[1].property_name").isEqualTo("last_name")
      .jsonPath("$.sort_by[1].direction").isEqualTo("DESC");
  }

  @Test
  void testDefaultPagingRequest() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/paging-request")
        .build()
      )
      .exchange()
      .expectBody()
      .jsonPath("$.page").isEqualTo(pagingProperties.getDefaultPage())
      .jsonPath("$.item_per_page").isEqualTo(pagingProperties.getDefaultItemPerPage())
      .jsonPath("$.sort_by").isEmpty();
  }

  @Test
  void testPaging() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/paging")
        .queryParam(pagingProperties.getQuery().getPageKey(), 1)
        .queryParam(pagingProperties.getQuery().getItemPerPageKey(), 100)
        .queryParam(pagingProperties.getQuery().getSortByKey(), "first_name:ASC,last_name:DESC")
        .build()
      )
      .exchange()
      .expectBody()
      .jsonPath("$.page").isEqualTo(1)
      .jsonPath("$.item_per_page").isEqualTo(100)
      .jsonPath("$.total_page").isEqualTo(100)
      .jsonPath("$.total_item").isEqualTo(100 * 100)
      .jsonPath("$.sort_by[0].property_name").isEqualTo("first_name")
      .jsonPath("$.sort_by[0].direction").isEqualTo("ASC")
      .jsonPath("$.sort_by[1].property_name").isEqualTo("last_name")
      .jsonPath("$.sort_by[1].direction").isEqualTo("DESC");
  }

  @Test
  void testDefaultPaging() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/paging")
        .build()
      )
      .exchange()
      .expectBody()
      .jsonPath("$.page").isEqualTo(pagingProperties.getDefaultPage())
      .jsonPath("$.item_per_page").isEqualTo(pagingProperties.getDefaultItemPerPage())
      .jsonPath("$.total_page").isEqualTo(100)
      .jsonPath("$.total_item").isEqualTo(100 * pagingProperties.getDefaultItemPerPage())
      .jsonPath("$.sort_by").isEmpty();
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ExampleController {

      @GetMapping(value = "/paging-request", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<PagingRequest> pagingRequest(PagingRequest pagingRequest) {
        return Mono.just(pagingRequest);
      }

      @GetMapping(value = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Paging> paging(PagingRequest pagingRequest) {
        return Mono.just(
          PagingHelper.toPaging(pagingRequest, 100, 100 * pagingRequest.getItemPerPage())
        );
      }

    }

  }
}
