package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.ExceptionClient;
import com.blibli.oss.backend.apiclient.client.model.FirstRequest;
import com.blibli.oss.backend.apiclient.client.model.FirstResponse;
import com.blibli.oss.backend.apiclient.client.model.SecondResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplication.class)
public class ExceptionClientFallbackTest {

  public static final FirstRequest FIRST_REQUEST = FirstRequest.builder()
    .name("Eko").build();

  @Autowired
  private ExceptionClient exceptionClient;

  @Test
  void testFirst() {
    FirstResponse response = exceptionClient.first(FIRST_REQUEST).block();
    assertEquals("Ups First", response.getHello());
  }

  @Test
  void testSecond() {
    SecondResponse response = exceptionClient.second().block();
    assertEquals("Ups Second", response.getHello());
  }
}
