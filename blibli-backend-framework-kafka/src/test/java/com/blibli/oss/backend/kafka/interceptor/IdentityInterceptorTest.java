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

package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import lombok.Builder;
import lombok.Data;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Eko Kurniawan Khannedy
 */
public class IdentityInterceptorTest {

  public static final String EVENT_ID = "eventId";
  public static final String EVENT_ID_VALUE = "id";
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private KafkaProperties kafkaProperties;

  @Mock
  private KafkaProperties.ModelProperties modelProperties;

  @InjectMocks
  private IdentityInterceptor identityInterceptor;

  private SampleData sampleData = SampleData.builder()
      .build();

  private ProducerEvent producerEvent = ProducerEvent.builder()
      .value(sampleData)
      .build();

  @Before
  public void setUp() throws Exception {
    when(modelProperties.getIdentity()).thenReturn(EVENT_ID);
    when(kafkaProperties.getModel()).thenReturn(modelProperties);
  }

  @Test
  public void testBeforeSend() {
    assertNull(sampleData.getEventId());
    identityInterceptor.beforeSend(producerEvent);
    assertNotNull(sampleData.getEventId());
  }

  @Test
  public void testDoNotInjectIfAlreadyExists() {
    sampleData.setEventId(EVENT_ID_VALUE);
    identityInterceptor.beforeSend(producerEvent);

    assertNotNull(sampleData.getEventId());
    assertEquals(EVENT_ID_VALUE, sampleData.getEventId());
  }

  @Test
  public void testInjectIfNotExists() {
    sampleData.setEventId(null);
    identityInterceptor.beforeSend(producerEvent);

    assertNotNull(sampleData.getEventId());
    assertNotEquals(EVENT_ID_VALUE, sampleData.getEventId());
  }

  @Data
  @Builder
  private static class SampleData {

    private String eventId;
  }
}