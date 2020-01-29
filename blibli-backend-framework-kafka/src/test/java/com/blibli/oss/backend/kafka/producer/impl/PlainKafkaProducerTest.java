/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blibli.oss.backend.kafka.producer.impl;

import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * @author Eko Kurniawan Khannedy
 */
public class PlainKafkaProducerTest {

  public static final String KEY = "key";

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ApplicationContext applicationContext;

  @Mock
  private KafkaProducerInterceptor kafkaProducerInterceptor;

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @Mock
  private Tracer tracer;

  @Mock
  private SendResult<String, String> sendResult;

  @InjectMocks
  private PlainKafkaProducerImpl kafkaProducer;

  private Request request;

  @Before
  public void setUp() throws Exception {
    mockObjectMapperSuccessWriteValueAsJson();
    when(tracer.getCurrentSpan()).thenReturn(null);

    request = Request.builder().id("ID").build();
  }

  private void mockObjectMapperSuccessWriteValueAsJson() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(anyObject()))
        .thenReturn("{}");
  }

  @Test
  public void testSendSuccess() throws Exception {
    mockKafkaTemplateWithSuccessResult();
    kafkaProducer.setApplicationContext(applicationContext);
    kafkaProducer.afterPropertiesSet();

    SendResult<String, String> value = kafkaProducer.send("TOPIC_NAME", request)
        .get();

    assertSame(sendResult, value);
  }

  private void mockKafkaTemplateWithSuccessResult() {
    SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
    future.set(sendResult);

    when(kafkaTemplate.send(any(ProducerRecord.class)))
        .thenReturn(future);
  }

  @Test(expected = RuntimeException.class)
  public void testSendError() throws Exception {
    mockKafkaTemplateWithExceptionResult();

    kafkaProducer.send("TOPIC_NAME", request)
        .get();
  }

  private void mockKafkaTemplateWithExceptionResult() {
    SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
    future.setException(new NullPointerException());

    when(kafkaTemplate.send(any(ProducerRecord.class)))
        .thenReturn(future);
  }

  @Test(expected = RuntimeException.class)
  public void testSendErrorBecuaseParsingJson() throws Exception {
    mockObjectMapperErrorParsing();

    kafkaProducer.send("TOPIC_NAME", request)
        .get();
  }

  private void mockObjectMapperErrorParsing() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(anyObject()))
        .thenThrow(new JsonParseException(null, "Parsing Error"));
  }

  @Test
  public void testSendSuccessWithInterceptor() throws Exception {
    mockKafkaTemplateWithSuccessResult();
    when(applicationContext.getBeansOfType(KafkaProducerInterceptor.class))
        .thenReturn(Collections.singletonMap(KEY, kafkaProducerInterceptor));

    kafkaProducer.setApplicationContext(applicationContext);
    kafkaProducer.afterPropertiesSet();
    SendResult<String, String> value = kafkaProducer.send("TOPIC_NAME", request)
        .get();

    assertSame(sendResult, value);
    verify(kafkaProducerInterceptor, times(1))
        .beforeSend(any(ProducerEvent.class));
  }

  @Data
  @Builder
  public static class Request {

    private String id;

    private Map<String, String> span;

    private String routingId;
  }

}