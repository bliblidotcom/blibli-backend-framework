package com.blibli.oss.backend.mandatoryparameter;

import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = TestApplication.class,
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
}
