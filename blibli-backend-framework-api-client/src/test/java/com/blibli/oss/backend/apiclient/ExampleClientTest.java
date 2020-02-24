package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.ExampleClient;
import com.blibli.oss.backend.apiclient.client.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TestApplication.class)
public class ExampleClientTest {

  public static final FirstRequest FIRST_REQUEST = FirstRequest.builder()
    .name("Eko Kurniawan").build();
  public static final FirstResponse FIRST_RESPONSE = FirstResponse.builder()
    .hello("Hello Eko Kurniawan")
    .build();
  public static final GenericResponse<String> GENERIC_RESPONSE = GenericResponse.<String>builder()
      .value("Test Generics")
      .build();
  public static final SecondResponse SECOND_RESPONSE = SecondResponse.builder().hello("Hello").build();
  private static WireMockServer wireMockServer;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private ExampleClient exampleClient;

  @BeforeAll
  static void beforeAll() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
  }

  @Test
  void testResponseEntityVoid() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/response-entity-void"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(
          aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    ResponseEntity<Void> response = exampleClient.responseEntityVoid().block();
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  void testResponseEntity() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/response-entity"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(
          aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    ResponseEntity<FirstResponse> response = exampleClient.responseEntity().block();
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
    assertEquals(FIRST_RESPONSE, response.getBody());
  }

  @Test
  void testResponseEntityList() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/response-entity-list"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(
          aResponse()
            .withStatus(400)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(Collections.singletonList(FIRST_RESPONSE)))
        )
    );

    ResponseEntity<List<FirstResponse>> response = exampleClient.responseEntityList().block();
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(FIRST_RESPONSE, response.getBody().get(0));
  }

  @Test
  void testFirst() throws JsonProcessingException {
    wireMockServer.stubFor(
      post(urlPathEqualTo("/first"))
        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalTo(objectMapper.writeValueAsString(FIRST_REQUEST)))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    FirstResponse response = exampleClient.first(FIRST_REQUEST).block();
    assertEquals(FIRST_RESPONSE, response);
  }

  @Test
  void testSecond() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/second"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(SECOND_RESPONSE))
        )
    );

    SecondResponse response = exampleClient.second().block();
    assertEquals(SECOND_RESPONSE, response);
  }

  @Test
  void testThird() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/third/eko"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    FirstResponse response = exampleClient.third("eko").block();
    assertEquals(FIRST_RESPONSE, response);
  }

  @Test
  void testForth() throws JsonProcessingException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/forth/eko"))
        .withQueryParam("page", equalTo("1"))
        .withQueryParam("size", equalTo("100"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader("X-API", equalTo("api"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    FirstResponse response = exampleClient.forth("eko", 1, 100, "api").block();
    assertEquals(FIRST_RESPONSE, response);
  }

  @Test
  void testFifth() throws JsonProcessingException {
    wireMockServer.stubFor(
      post(urlPathEqualTo("/fifth"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(containing("firstName"))
        .withRequestBody(containing("lastName"))
        .withRequestBody(containing("Eko"))
        .withRequestBody(containing("Khannedy"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("firstName", "Eko");
    form.add("lastName", "Khannedy");

    FirstResponse response = exampleClient.fifth(form).block();
    assertEquals(FIRST_RESPONSE, response);
  }

  @Test
  void testSixth() throws JsonProcessingException {
    wireMockServer.stubFor(
      post(urlPathEqualTo("/sixth"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.MULTIPART_FORM_DATA_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(containing("file"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(FIRST_RESPONSE))
        )
    );

    Resource resource = resourceLoader.getResource("classpath:/upload.txt");
    FirstResponse response = exampleClient.sixth(resource).block();
    assertEquals(FIRST_RESPONSE, response);
  }

  @Test
  void testGenerics() throws JsonProcessingException {
    wireMockServer.stubFor(
      post(urlPathEqualTo("/generics"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalTo("testing"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(GENERIC_RESPONSE))
        )
    );

    GenericResponse<String> response = exampleClient.generic("testing").block();
    assertEquals(GENERIC_RESPONSE, response);
  }

  @Test
  void testGenericsTwo() throws JsonProcessingException {
    wireMockServer.stubFor(
      post(urlPathEqualTo("/generics-two"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalTo("testing"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(Collections.singletonList("testing")))
      )
    );

    List<String> response = exampleClient.genericTwo("testing").block();
    assertEquals(Collections.singletonList("testing"), response);
  }

  @Test
  void testInheritedType() throws JsonProcessingException {
    InheritedResponse expectedResponse = InheritedResponse.inheritedBuilder()
        .name("testing")
        .detail("testing-detail")
        .build();

    wireMockServer.stubFor(
      post(urlPathEqualTo("/inherited"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalTo("testing"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(expectedResponse))
        )
    );

    InheritedResponse response = exampleClient.inherited("testing").block();
    assertEquals(expectedResponse, response);
  }

  @Test
  public void testGenericInheritedType() throws JsonProcessingException {
    InheritedResponse wrapped = InheritedResponse.inheritedBuilder()
      .name("testing")
      .detail("testing-detail")
      .build();

    GenericResponse<InheritedResponse> expectedResponse = GenericResponse.<InheritedResponse>builder()
      .value(wrapped)
      .build();

    wireMockServer.stubFor(
      post(urlPathEqualTo("/generic-inherited"))
        .withHeader(HttpHeaders.CONTENT_TYPE, containing(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalTo("testing"))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(expectedResponse))
        )
    );

    GenericResponse<InheritedResponse> response = exampleClient.genericInherited("testing").block();
    assertEquals(expectedResponse, response);
  }

  @AfterAll
  static void afterAll() {
    wireMockServer.stop();
  }
}
