package com.blibli.oss.backend.json.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonHelper {

  private ObjectMapper objectMapper;

  public JsonHelper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @SneakyThrows
  public <T> T fromJson(String json, Class<T> tClass) {
    return objectMapper.readValue(json, tClass);
  }

  @SneakyThrows
  public <T> T fromJson(String json, TypeReference<T> tTypeReference) {
    return objectMapper.readValue(json, tTypeReference);
  }

  @SneakyThrows
  public <T> String toJson(T object) {
    return objectMapper.writeValueAsString(object);
  }

}
