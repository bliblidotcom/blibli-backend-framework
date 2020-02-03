package com.blibli.oss.backend.kafka.producer;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.model.ProducerEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class KafkaProducer {

  private KafkaTemplate<String, String> kafkaTemplate;

  private ObjectMapper objectMapper;

  private List<KafkaProducerInterceptor> producerInterceptors;

  public Disposable sendAndSubscribe(String topic, String key, Object value, Scheduler scheduler) {
    return sendOn(topic, key, value, scheduler)
      .subscribe();
  }

  public Disposable sendAndSubscribe(ProducerEvent producerEvent, Scheduler scheduler) {
    return sendOn(producerEvent, scheduler)
      .subscribe();
  }

  public Mono<SendResult<String, String>> sendOn(String topic, String key, Object value, Scheduler scheduler) {
    return send(topic, key, value)
      .subscribeOn(scheduler);
  }

  public Mono<SendResult<String, String>> sendOn(ProducerEvent producerEvent, Scheduler scheduler) {
    return send(producerEvent)
      .subscribeOn(scheduler);
  }

  public Mono<SendResult<String, String>> send(String topic, String key, Object value) {
    return Mono.fromCallable(() -> ProducerEvent.builder().topic(topic).key(key).value(value).build())
      .flatMap(this::send);
  }

  public Mono<SendResult<String, String>> send(ProducerEvent producerEvent) {
    return Mono.just(producerEvent)
      .map(event -> InterceptorUtil.fireBeforeSend(event, producerInterceptors))
      .flatMap(event -> sendWithKafkaTemplate(toProducerRecord(event)));
  }

  private Mono<SendResult<String, String>> sendWithKafkaTemplate(ProducerRecord<String, String> producerRecord) {
    return Mono.create(sink -> kafkaTemplate.send(producerRecord)
      .addCallback(sink::success, sink::error));
  }

  private ProducerRecord<String, String> toProducerRecord(ProducerEvent event) {
    return new ProducerRecord<>(
      event.getTopic(),
      event.getPartition(),
      event.getTimestamp(),
      event.getKey(),
      getValue(event),
      getHeaders(event)
    );
  }

  private Headers getHeaders(ProducerEvent event) {
    if (Objects.isNull(event.getHeaders())) {
      return new RecordHeaders();
    } else {
      return event.getHeaders();
    }
  }

  @SneakyThrows
  private String getValue(ProducerEvent event) {
    if (event.getValue() instanceof String) {
      return (String) event.getValue();
    } else {
      return objectMapper.writeValueAsString(event.getValue());
    }
  }

}