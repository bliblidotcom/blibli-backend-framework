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

import com.blibli.oss.backend.kafka.interceptor.events.ConsumerEvent;
import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.Ordered;

import static org.mockito.Mockito.*;

/**
 * @author Eko Kurniawan Khannedy
 */
public class LogInterceptorTest {

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock
  private KafkaProperties.LogProperties logProperties;

  @Mock
  private ProducerEvent producerEvent;

  @Mock
  private ConsumerEvent consumerEvent;

  @InjectMocks
  private LogInterceptor logInterceptor;

  @Before
  public void setUp() throws Exception {
    when(logProperties.isBeforeConsume()).thenReturn(true);
    when(logProperties.isBeforeSend()).thenReturn(true);
  }

  @Test
  public void testBeforeConsume() {
    logInterceptor.beforeConsume(consumerEvent);

    verify(consumerEvent, times(1)).getTopic();
    verify(consumerEvent, times(1)).getValue();
  }

  @Test
  public void testBeforeSend() {
    logInterceptor.beforeSend(producerEvent);

    verify(producerEvent, times(1)).getTopic();
    verify(producerEvent, times(1)).getValue();
  }

  @Test
  public void testGetOrder() {
    Assert.assertEquals(Ordered.LOWEST_PRECEDENCE, logInterceptor.getOrder());
  }
}