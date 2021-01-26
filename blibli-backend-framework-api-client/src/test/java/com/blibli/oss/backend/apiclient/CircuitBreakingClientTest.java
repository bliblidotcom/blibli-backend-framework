package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.HelloApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplication.class)
public class CircuitBreakingClientTest {

  // TODO create dedicated api client with small windowSize, so testing is easier
  @Autowired
  private HelloApiClient helloApiClient;

  @Test
  void testFallback() {
    String response;
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();

    // From now, circuit is closed
    // TODO Assert circuitbreaker state machine
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
    response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
  }
}
