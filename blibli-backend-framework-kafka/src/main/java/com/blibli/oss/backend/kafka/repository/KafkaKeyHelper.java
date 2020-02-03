package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaKeyHelper {

  private final static Map<String, Field> fieldCache = new ConcurrentHashMap<>();

  @SneakyThrows
  public static String getKafkaKey(Object data) {
    Field field = doFindField(data);
    if (field != null) {
      return (String) field.get(data);
    } else {
      return null;
    }
  }

  private static Field doFindField(Object data) {
    String name = data.getClass().getName();
    Field field = fieldCache.get(name);

    if (field != null) {
      return field;
    }

    return fieldCache.computeIfAbsent(name, s -> {
      Field[] fields = data.getClass().getDeclaredFields();
      for (Field value : fields) {
        KafkaKey annotation = value.getAnnotation(KafkaKey.class);
        if (Objects.nonNull(annotation)) {
          value.setAccessible(true);
          return value;
        }
      }
      return null;
    });
  }

}
