package com.blibli.oss.backend.kafka.producer.impl;

import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class KafkaProducerImplTest {

  public static final String TOPIC = "topic";
  public static final String KEY = "key";
  public static final String MESSAGE = "object";
  public static final Integer PARTITION = 1;
  public static final Long TIMESTAMP = 1L;
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private PlainKafkaProducer plainKafkaProducer;

  @Mock
  private SendResult<String, String> sendResult;

  @InjectMocks
  private KafkaProducerImpl kafkaProducer;

  @Test
  public void testSend() {
    SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
    future.set(sendResult);

    when(plainKafkaProducer.send(TOPIC, KEY, MESSAGE, PARTITION, TIMESTAMP))
      .thenReturn(future);

    SendResult<String, String> result = kafkaProducer.send(TOPIC, KEY, MESSAGE, PARTITION, TIMESTAMP).toBlocking().value();

    assertSame(result, sendResult);
  }
}