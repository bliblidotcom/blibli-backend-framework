package com.blibli.oss.backend.apiclient;

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
public class SleuthApiClientTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testSleuth() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/first")
        .queryParam("firstName", "Eko")
        .queryParam("lastName", "Khannedy")
        .build())
      .exchange()
      .expectStatus().is2xxSuccessful()
      .expectBody()
      .jsonPath("$.headerFirstName").isEqualTo("Eko")
      .jsonPath("$.headerLastName").isEqualTo("Khannedy")
      .jsonPath("$.firstName").isEqualTo("Eko")
      .jsonPath("$.lastName").isEqualTo("Khannedy");
  }
}
