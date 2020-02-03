package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
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
      List<Field> fieldsListWithAnnotation = FieldUtils.getFieldsListWithAnnotation(data.getClass(), KafkaKey.class);
      if (!fieldsListWithAnnotation.isEmpty()) {
        Field value = fieldsListWithAnnotation.get(0);
        value.setAccessible(true);
        return value;
      }
      return null;
    });
  }

}
