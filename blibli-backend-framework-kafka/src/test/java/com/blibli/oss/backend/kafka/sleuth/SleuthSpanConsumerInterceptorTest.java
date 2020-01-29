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

package com.blibli.oss.backend.kafka.sleuth;

import com.blibli.oss.backend.kafka.interceptor.events.ConsumerEvent;
import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Eko Kurniawan Khannedy
 */
public class SleuthSpanConsumerInterceptorTest {

  public static final String SPAN = "span";
  public static final Span SPAN_OBJECT = Span.builder()
    .processId("processId")
    .spanId(41841094L)
    .traceId(234248923L)
    .name("name")
    .build();
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private KafkaProperties.ModelProperties modelProperties;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private Tracer tracer;

  @Mock
  private SleuthProperties sleuthProperties;

  private SampleData sampleData = SampleData.builder()
    .build();

  private ProducerEvent producerEvent = ProducerEvent.builder()
    .value(sampleData)
    .build();

  private ConsumerEvent consumerEvent = ConsumerEvent.builder()
    .build();

  private SleuthSpanConsumerInterceptor sleuthSpanProducerInterceptor;

  @Before
  public void setUp() throws Exception {
    sleuthSpanProducerInterceptor = new SleuthSpanConsumerInterceptor(modelProperties, objectMapper, tracer, sleuthProperties);

    when(modelProperties.getTrace()).thenReturn(SPAN);
  }

  @Test
  public void beforeConsumeWithContinueSpan() throws JsonProcessingException {
    when(sleuthProperties.isSupportsJoin()).thenReturn(false);

    Map<String, String> spanMap = SleuthHelper.toMap(SPAN_OBJECT);

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("span", spanMap);

    String json = objectMapper.writeValueAsString(jsonMap);

    consumerEvent.setValue(json);

    sleuthSpanProducerInterceptor.beforeConsume(consumerEvent);

    verify(tracer).continueSpan(any(Span.class));
  }

  @Test
  public void beforeConsumeWithJoinSpan() throws JsonProcessingException {
    when(sleuthProperties.isSupportsJoin()).thenReturn(true);

    Map<String, String> spanMap = SleuthHelper.toMap(SPAN_OBJECT);

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("span", spanMap);

    String json = objectMapper.writeValueAsString(jsonMap);

    consumerEvent.setValue(json);

    sleuthSpanProducerInterceptor.beforeConsume(consumerEvent);

    verify(tracer).createSpan(eq("name"), any(Span.class));
  }

  @Test
  public void testNoSpan() throws JsonProcessingException {
    when(sleuthProperties.isSupportsJoin()).thenReturn(true);

    Map<String, Object> jsonMap = new HashMap<>();

    String json = objectMapper.writeValueAsString(jsonMap);

    consumerEvent.setValue(json);

    sleuthSpanProducerInterceptor.beforeConsume(consumerEvent);

    verify(tracer).createSpan("kafka:consumer:" + consumerEvent.getTopic());
    verify(tracer).close(null);
    verify(tracer).getCurrentSpan();
  }

  @After
  public void tearDown() throws Exception {
    verifyNoMoreInteractions(tracer);
  }

  @Test
  public void testGetOrder() {
    assertEquals(Ordered.HIGHEST_PRECEDENCE, sleuthSpanProducerInterceptor.getOrder());
  }

  @Data
  @Builder
  private static class SampleData {

    private Map<String, String> span = Collections.singletonMap("Test", "Test");
  }
}