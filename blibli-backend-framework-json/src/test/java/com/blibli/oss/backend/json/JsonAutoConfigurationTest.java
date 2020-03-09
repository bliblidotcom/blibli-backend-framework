package com.blibli.oss.backend.json;

import com.blibli.oss.backend.json.aware.JsonAware;
import com.blibli.oss.backend.json.helper.JsonHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JsonAutoConfigurationTest.Application.class)
class JsonAutoConfigurationTest {

  @Autowired(required = false)
  private JsonHelper jsonHelper;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplication webApplication;

  @Test
  void testJsonHelper() {
    assertNotNull(jsonHelper);
  }

  @Test
  void testHello() throws JsonProcessingException {
    HelloResponse helloResponse = HelloResponse.builder()
      .name("Eko Kurniawan Khannedy")
      .build();

    assertEquals(jsonHelper.toJson(helloResponse), objectMapper.writeValueAsString(helloResponse));
  }

  @Test
  void testWebApplication() {
    assertNotNull(webApplication.getJsonHelper());
  }

  @Test
  void testWriteTypeReference() {
    List<String> strings = Arrays.asList("Eko", "Kurniawan", "Khannedy");
    String json = webApplication.getJsonHelper().toJson(strings);

    List<String> list = webApplication.getJsonHelper().fromJson(json, new TypeReference<List<String>>() {
    });

    assertEquals(strings, list);
  }

  @Test
  void testFromJson() {
    HelloResponse helloResponse = HelloResponse.builder()
      .name("Eko Kurniawan Khannedy")
      .build();
    String json = webApplication.getJsonHelper().toJson(helloResponse);

    HelloResponse response = webApplication.getJsonHelper().fromJson(json, HelloResponse.class);
    assertEquals(helloResponse, response);
  }

  @SpringBootApplication
  static class Application {

    @Bean
    public WebApplication webApplication() {
      return new WebApplication();
    }

  }

  static class WebApplication implements JsonAware {

    @Setter
    @Getter
    private JsonHelper jsonHelper;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  static class HelloResponse {

    private String name;

  }

}