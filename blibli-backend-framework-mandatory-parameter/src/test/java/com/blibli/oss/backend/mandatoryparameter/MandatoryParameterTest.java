package com.blibli.oss.backend.mandatoryparameter;

import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = TestApplication.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class MandatoryParameterTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private MandatoryParameterProperties properties;

  @Test
  void testMandatoryParameterUsingQuery() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/query")
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
  void testMandatoryParameterUsingHeader() {
    webTestClient.get()
      .uri("/header")
      .header(properties.getHeaderKey().getStoreId(), "storeId")
      .header(properties.getHeaderKey().getChannelId(), "channelId")
      .header(properties.getHeaderKey().getClientId(), "clientId")
      .header(properties.getHeaderKey().getUsername(), "username")
      .header(properties.getHeaderKey().getRequestId(), "requestId")
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
  void testMandatoryParameterSwagger() {
    webTestClient.get()
      .uri("/v3/api-docs")
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.components.parameters.queryParameterStoreId").isNotEmpty()
      .jsonPath("$.components.parameters.queryParameterChannelId").isNotEmpty()
      .jsonPath("$.components.parameters.queryParameterClientId").isNotEmpty()
      .jsonPath("$.components.parameters.queryParameterUsername").isNotEmpty()
      .jsonPath("$.components.parameters.queryParameterRequestId").isNotEmpty()

      .jsonPath("$.components.parameters.headerParameterStoreId").isNotEmpty()
      .jsonPath("$.components.parameters.headerParameterChannelId").isNotEmpty()
      .jsonPath("$.components.parameters.headerParameterClientId").isNotEmpty()
      .jsonPath("$.components.parameters.headerParameterUsername").isNotEmpty()
      .jsonPath("$.components.parameters.headerParameterRequestId").isNotEmpty()

      .jsonPath("$.paths./query.get.parameters").isArray()
      .jsonPath("$.paths./query.get.parameters[*]name").value(Matchers.contains("storeId", "channelId", "clientId", "username", "requestId"))

      .jsonPath("$.paths./header.get.parameters").isArray()
      .jsonPath("$.paths./header.get.parameters[*]name").value(Matchers.contains("storeId", "channelId", "clientId", "username", "requestId"));
  }
}
