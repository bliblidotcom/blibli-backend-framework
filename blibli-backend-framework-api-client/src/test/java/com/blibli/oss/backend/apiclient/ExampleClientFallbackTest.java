package com.blibli.oss.backend.apiclient;

import com.blibli.oss.backend.apiclient.client.ExampleClient;
import com.blibli.oss.backend.apiclient.client.model.FirstRequest;
import com.blibli.oss.backend.apiclient.client.model.FirstResponse;
import com.blibli.oss.backend.apiclient.client.model.SecondResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
public class ExampleClientFallbackTest {

  public static final FirstRequest FIRST_REQUEST = FirstRequest.builder()
    .name("Eko").build();

  @Autowired
  private ExampleClient exampleClient;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  void testFirst() {
    FirstResponse response = exampleClient.first(FIRST_REQUEST).block();
    assertEquals("Ups First", response.getHello());
  }

  @Test
  void testSecond() {
    SecondResponse response = exampleClient.second().block();
    assertEquals("Ups Second", response.getHello());
  }

  @Test
  void testThird() {
    FirstResponse response = exampleClient.third("eko").block();
    assertEquals("Ups Third", response.getHello());
  }

  @Test
  void testForth() {
    FirstResponse response = exampleClient.forth("eko", 1, 100, "api").block();
    assertEquals("Ups Forth", response.getHello());
  }

  @Test
  void testFifth() {
    FirstResponse response = exampleClient.fifth(new LinkedMultiValueMap<>()).block();
    assertEquals("Ups Fifth", response.getHello());
  }

  @Test
  void testSixth() {
    FirstResponse response = exampleClient.sixth(resourceLoader.getResource("classpath:/upload.txt")).block();
    assertEquals("Ups Sixth", response.getHello());
  }
}
