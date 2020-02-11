package com.blibli.oss.backend.aggregate.query.example;

import com.blibli.oss.backend.aggregate.query.constant.AggregateQueryConstant;
import com.blibli.oss.backend.aggregate.query.model.AggregateQueryResponse;
import com.blibli.oss.backend.aggregate.query.properties.AggregateQueryProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ExampleApplication.class)
public class ExampleTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private ExampleAggregateQueryApiClient exampleAggregateQueryApiClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private AggregateQueryProperties properties;

  @BeforeAll
  static void beforeAll() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
  }

  @AfterAll
  static void afterAll() {
    wireMockServer.stop();
  }

  @Test
  void testDetail() throws IOException {
    wireMockServer.stubFor(
      get(urlPathEqualTo("/api-native/index/1"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(AggregateQueryConstant.SERVICE_ID_HEADER, equalTo(properties.getServiceId()))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(IOUtils.toByteArray(resourceLoader.getResource("classpath:/detail.json").getInputStream()))
        )
    );

    Mono<ExampleResponse> response = exampleAggregateQueryApiClient.get("index", "1")
      .map(value -> value.sourceAs(objectMapper, ExampleResponse.class));

    StepVerifier.create(response)
      .expectNext(ExampleResponse.builder().id(1).firstName("Eko").middleName("Kurniawan").lastName("Khannedy").build())
      .expectComplete()
      .verify();
  }

  @Test
  void testSearch() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
    sourceBuilder.from(0);
    sourceBuilder.size(5);
    String request = sourceBuilder.toString();

    wireMockServer.stubFor(
      post(urlPathEqualTo("/api-native/index/_search"))
        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(AggregateQueryConstant.SERVICE_ID_HEADER, equalTo(properties.getServiceId()))
        .withRequestBody(equalTo(request))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(IOUtils.toByteArray(resourceLoader.getResource("classpath:/search.json").getInputStream()))
        )
    );

    Flux<ExampleResponse> response = exampleAggregateQueryApiClient.search("index", request)
      .map(value -> value.getHits().hitsAs(objectMapper, ExampleResponse.class))
      .flatMapMany(Flux::fromIterable);

    StepVerifier.create(response)
      .expectNext(ExampleResponse.builder().id(1).firstName("Eko").middleName("Kurniawan").lastName("Khannedy").build())
      .expectNext(ExampleResponse.builder().id(2).firstName("Joko").middleName("").lastName("Morro").build())
      .expectComplete()
      .verify();
  }

  @Test
  void testScroll() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
    sourceBuilder.from(0);
    sourceBuilder.size(5);
    String request = sourceBuilder.toString();

    wireMockServer.stubFor(
      post(urlPathEqualTo("/api-native/index/_scroll"))
        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(AggregateQueryConstant.SERVICE_ID_HEADER, equalTo(properties.getServiceId()))
        .withRequestBody(equalTo(request))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(IOUtils.toByteArray(resourceLoader.getResource("classpath:/search.json").getInputStream()))
        )
    );

    Flux<ExampleResponse> response = exampleAggregateQueryApiClient.scroll("index", request)
      .map(value -> value.getHits().hitsAs(objectMapper, ExampleResponse.class))
      .flatMapMany(Flux::fromIterable);

    StepVerifier.create(response)
      .expectNext(ExampleResponse.builder().id(1).firstName("Eko").middleName("Kurniawan").lastName("Khannedy").build())
      .expectNext(ExampleResponse.builder().id(2).firstName("Joko").middleName("").lastName("Morro").build())
      .expectComplete()
      .verify();
  }

  @Test
  void testScrollNext() throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
    sourceBuilder.from(0);
    sourceBuilder.size(5);
    String request = sourceBuilder.toString();

    wireMockServer.stubFor(
      post(urlPathEqualTo("/api-native/index/_scroll"))
        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(AggregateQueryConstant.SERVICE_ID_HEADER, equalTo(properties.getServiceId()))
        .withRequestBody(equalTo(request))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(IOUtils.toByteArray(resourceLoader.getResource("classpath:/scroll.json").getInputStream()))
        )
    );

    wireMockServer.stubFor(
      get(urlPathEqualTo("/api-native/index/_scroll/DnF1ZXJ5VGhlbkZldGNoBQAAAAAA"))
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withHeader(AggregateQueryConstant.SERVICE_ID_HEADER, equalTo(properties.getServiceId()))
        .willReturn(
          aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(IOUtils.toByteArray(resourceLoader.getResource("classpath:/scroll.json").getInputStream()))
        )
    );

    Flux<ExampleResponse> response = exampleAggregateQueryApiClient.scroll("index", request)
      .map(AggregateQueryResponse::getScrollId)
      .flatMap(value -> exampleAggregateQueryApiClient.nextScroll("index", value))
      .map(value -> value.getHits().hitsAs(objectMapper, ExampleResponse.class))
      .flatMapMany(Flux::fromIterable);

    StepVerifier.create(response)
      .expectNext(ExampleResponse.builder().id(1).firstName("Eko").middleName("Kurniawan").lastName("Khannedy").build())
      .expectNext(ExampleResponse.builder().id(2).firstName("Joko").middleName("").lastName("Morro").build())
      .expectComplete()
      .verify();
  }

}
