package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.HelloApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
public class HelloApiClientFallbackTest {

  @Autowired
  private HelloApiClient helloApiClient;

  @Test
  void testResponseEntityVoid() {
    String response = helloApiClient.hello().block();
    assertEquals("Fallback", response);
  }

}
