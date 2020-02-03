package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaHelper {

  private final static Map<String, Field> fieldCache = new ConcurrentHashMap<>();

  private final static Map<String, String> topicCache = new ConcurrentHashMap<>();

  public static String getTopic(Class<?> aClass) {
    String name = aClass.getName();
    String topic = topicCache.get(name);

    if (topic != null) {
      return topic;
    }

    return topicCache.computeIfAbsent(name, s -> {
      KafkaTopic annotation = AnnotationUtils.findAnnotation(aClass, KafkaTopic.class);
      if (annotation != null) {
        return annotation.value();
      } else {
        return null;
      }
    });
  }

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
