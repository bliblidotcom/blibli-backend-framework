package com.blibli.oss.backend.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JobTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void testFirst() {
    String[] arguments = "reindex --start=100 --from=200".split("\\s+");

    String command = arguments[0];
    Map<String, String> map = new HashMap<>();

    for (int i = 1; i < arguments.length; i++) {
      String[] split = arguments[i].split("=");
      if (split.length == 2 && split[0].startsWith("--")) {
        map.put(split[0].substring(2), split[1]);
      }
    }

    Request request = objectMapper.convertValue(map, Request.class);
    log.info(request.toString());
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Request {

    private Integer start;

    private Integer from;

  }
}