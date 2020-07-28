package com.blibli.oss.backend.newrelic.integration;

import com.blibli.oss.backend.newrelic.TestApplication;
import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class NewRelicIntegrationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private WebTestClient webTestClient;

  @SpyBean
  private Agent newRelicAgent;

  @MockBean
  private Transaction transaction;

  @MockBean
  private TracedMethod tracedMethod;

  @MockBean
  private Segment segment;

  @MockBean
  private Token token;

  @MockBean
  private MongoProperties mongoProperties;

  @MockBean
  private ReactiveMongoTemplate reactiveMongoTemplate;

  private static String NAME = "NAMA";

  @BeforeEach
  public void setup() {
    initMocks(this);
    WebTestClient.bindToApplicationContext(applicationContext);
  }

  @Test
  public void testFilter() {
    ArgumentCaptor<String> segmentNameCaptor = ArgumentCaptor.forClass(String.class);

    when(newRelicAgent.getTransaction())
        .thenReturn(transaction);
    when(transaction.getTracedMethod())
        .thenReturn(tracedMethod);
    when(transaction.getToken())
        .thenReturn(token);
    when(transaction.startSegment(segmentNameCaptor.capture()))
        .thenReturn(segment);

    webTestClient
        .get()
        .uri(builder -> builder
            .path("/test")
            .queryParam("name", NAME)
            .build())

        .exchange()

        .expectBody()
        .jsonPath("$.data")
        .isEqualTo("I say Hello World to NAMA");

    Assert.assertEquals("Command HelloWorldCommandImpl.HelloWorldCommandImpl.execute(..)", segmentNameCaptor.getValue());

    verify(newRelicAgent).getTransaction();
    verify(transaction).startSegment(segmentNameCaptor.capture());
    verify(segment).end();
    verify(transaction).getToken();
    verify(token).link();
    verify(transaction).getTracedMethod();
    verify(tracedMethod).setMetricName("Reactor");
  }

  @AfterEach
  public void after() {
    verifyNoMoreInteractions(newRelicAgent, transaction, tracedMethod, token, segment);
  }

}
