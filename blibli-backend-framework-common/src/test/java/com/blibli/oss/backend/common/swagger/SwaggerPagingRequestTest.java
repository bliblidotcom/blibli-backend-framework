package com.blibli.oss.backend.common.swagger;

import com.blibli.oss.backend.common.helper.PagingHelper;
import com.blibli.oss.backend.common.model.request.PagingRequest;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.common.swagger.annotation.PagingRequestInQuery;
import org.hamcrest.Matchers;
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
  classes = SwaggerPagingRequestTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SwaggerPagingRequestTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testPaging() {
    webTestClient.get()
      .uri("/v3/api-docs")
      .exchange()
      .expectBody()
      .jsonPath("$.paths./paging.get.parameters[*].name").value(Matchers.contains("page", "itemPerPage", "sortBy"))
      .jsonPath("$.paths./paging-request.get.parameters[*].name").value(Matchers.contains("page", "itemPerPage", "sortBy"))
      .jsonPath("$.components.parameters.queryPagingRequestPage").isNotEmpty()
      .jsonPath("$.components.parameters.queryPagingRequestItemPerPage").isNotEmpty()
      .jsonPath("$.components.parameters.queryPagingRequestSortBy").isNotEmpty();
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class PagingController {

      @PagingRequestInQuery
      @GetMapping(value = "/paging-request", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<PagingRequest> pagingRequest(PagingRequest pagingRequest) {
        return Mono.just(pagingRequest);
      }

      @PagingRequestInQuery
      @GetMapping(value = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Paging> paging(PagingRequest pagingRequest) {
        return Mono.just(
          PagingHelper.toPaging(pagingRequest, 100, 100 * pagingRequest.getItemPerPage())
        );
      }

    }

  }
}
