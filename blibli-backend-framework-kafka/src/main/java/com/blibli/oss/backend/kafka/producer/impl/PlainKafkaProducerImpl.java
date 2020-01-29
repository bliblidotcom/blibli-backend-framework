package com.blibli.oss.backend.kafka.producer.impl;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.List;

@Slf4j
public class PlainKafkaProducerImpl implements PlainKafkaProducer, InitializingBean, ApplicationContextAware {

  private ObjectMapper objectMapper;

  private KafkaTemplate<String, String> kafkaTemplate;

  private ApplicationContext applicationContext;

  private List<KafkaProducerInterceptor> kafkaProducerInterceptors;

  public PlainKafkaProducerImpl(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
    this.objectMapper = objectMapper;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    kafkaProducerInterceptors = InterceptorUtil.getKafkaProducerInterceptors(applicationContext);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public ListenableFuture<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp) {
    try {
      ProducerEvent event = ProducerEvent.builder()
        .key(key)
        .topic(topic)
        .value(message)
        .partition(partition)
        .timestamp(timestamp)
        .build();

      fireBeforeSend(event);

      String json = objectMapper.writeValueAsString(event.getValue());
      return kafkaTemplate.send(
        new ProducerRecord<>(event.getTopic(), event.getPartition(), event.getTimestamp(), event.getKey(), json)
      );
    } catch (JsonProcessingException e) {
      SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
      future.setException(e);
      return future;
    }
  }

  private void fireBeforeSend(ProducerEvent event) {
    for (KafkaProducerInterceptor interceptor : kafkaProducerInterceptors) {
      try {
        interceptor.beforeSend(event);
      } catch (Throwable throwable) {
        log.error("Error while invoking interceptor", throwable);
      }
    }
  }
}
