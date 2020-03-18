package com.blibli.oss.backend.mandatoryparameter.apiclient;

import com.blibli.oss.backend.mandatoryparameter.apiclient.apiclient.FirstApiClient;
import com.blibli.oss.backend.mandatoryparameter.apiclient.apiclient.SecondApiClient;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = MandatoryParameterApiClientTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, // https://github.com/spring-projects/spring-boot/issues/5077
  properties = {
    "server.port=15234"
  }
)
public class MandatoryParameterApiClientTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private MandatoryParameterProperties properties;

  @Test
  void testFirst() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/test-first")
        .queryParam(properties.getQueryKey().getStoreId(), "storeId")
        .queryParam(properties.getQueryKey().getChannelId(), "channelId")
        .queryParam(properties.getQueryKey().getClientId(), "clientId")
        .queryParam(properties.getQueryKey().getUsername(), "username")
        .queryParam(properties.getQueryKey().getRequestId(), "requestId")
        .build())
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.storeId").isEqualTo("storeId")
      .jsonPath("$.channelId").isEqualTo("channelId")
      .jsonPath("$.clientId").isEqualTo("clientId")
      .jsonPath("$.username").isEqualTo("username")
      .jsonPath("$.requestId").isEqualTo("requestId");
  }

  @Test
  void testSecond() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/test-second")
        .queryParam(properties.getQueryKey().getStoreId(), "storeId")
        .queryParam(properties.getQueryKey().getChannelId(), "channelId")
        .queryParam(properties.getQueryKey().getClientId(), "clientId")
        .queryParam(properties.getQueryKey().getUsername(), "username")
        .queryParam(properties.getQueryKey().getRequestId(), "requestId")
        .build())
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.storeId").isEqualTo("storeId")
      .jsonPath("$.channelId").isEqualTo("channelId")
      .jsonPath("$.clientId").isEqualTo("clientId")
      .jsonPath("$.username").isEqualTo("username")
      .jsonPath("$.requestId").isEqualTo("requestId");
  }

  @SpringBootApplication
  public static class Application {

    @RestController
    public static class ApiClientController {

      @Autowired
      private FirstApiClient firstApiClient;

      @Autowired
      private SecondApiClient secondApiClient;

      @Autowired
      private MandatoryParameterProperties properties;

      @GetMapping(value = "/first", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Map<String, String>> first(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
          MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
          Map<String, String> map = new HashMap<>();
          map.put(properties.getQueryKey().getStoreId(), queryParams.getFirst(properties.getQueryKey().getStoreId()));
          map.put(properties.getQueryKey().getClientId(), queryParams.getFirst(properties.getQueryKey().getClientId()));
          map.put(properties.getQueryKey().getChannelId(), queryParams.getFirst(properties.getQueryKey().getChannelId()));
          map.put(properties.getQueryKey().getUsername(), queryParams.getFirst(properties.getQueryKey().getUsername()));
          map.put(properties.getQueryKey().getRequestId(), queryParams.getFirst(properties.getQueryKey().getRequestId()));
          return map;
        });
      }

      @GetMapping(value = "/second", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Map<String, String>> second(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
          HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
          Map<String, String> map = new HashMap<>();
          map.put(properties.getHeaderKey().getStoreId(), httpHeaders.getFirst(properties.getHeaderKey().getStoreId()));
          map.put(properties.getHeaderKey().getClientId(), httpHeaders.getFirst(properties.getHeaderKey().getClientId()));
          map.put(properties.getHeaderKey().getChannelId(), httpHeaders.getFirst(properties.getHeaderKey().getChannelId()));
          map.put(properties.getHeaderKey().getUsername(), httpHeaders.getFirst(properties.getHeaderKey().getUsername()));
          map.put(properties.getHeaderKey().getRequestId(), httpHeaders.getFirst(properties.getHeaderKey().getRequestId()));
          return map;
        });
      }

      @GetMapping(value = "/test-first", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Map<String, String>> testFirst() {
        return firstApiClient.first();
      }

      @GetMapping(value = "/test-second", produces = MediaType.APPLICATION_JSON_VALUE)
      public Mono<Map<String, String>> testSecond() {
        return secondApiClient.second();
      }

    }

  }
}
