package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.DynamicClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(classes = TestApplication.class)
public class DynamicClientTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private DynamicClient dynamicClient;

  @BeforeAll
  static void beforeAll() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
  }

  @Test
  void testResponseEntityVoid() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/dynamic"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.TEXT_PLAIN_VALUE))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBody("SUCCESS")
        )
    );

    StepVerifier.create(dynamicClient.dynamic("http://localhost:8089"))
      .expectNext("SUCCESS")
      .verifyComplete();
  }

  @AfterAll
  static void afterAll() {
    wireMockServer.stop();
  }
}
