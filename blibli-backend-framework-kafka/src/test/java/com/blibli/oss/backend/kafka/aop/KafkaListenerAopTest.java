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

package com.blibli.oss.backend.kafka.aop;

import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import com.blibli.oss.backend.kafka.interceptor.events.ConsumerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Eko Kurniawan Khannedy
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaListenerAopTest.Application.class)
@TestPropertySource(properties = {
    "kafka.plugin.aspectj=false"
})
public class KafkaListenerAopTest {

  public static final String EVENT_ID = "EVENT_ID";

  @MockBean
  private KafkaTemplate<String, String> kafkaTemplate;

  @MockBean
  private MyInterceptor myInterceptor;

  private SampleData sampleData = SampleData.builder()
      .eventId(EVENT_ID)
      .build();

  @Autowired
  private MyListener myListener;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testInvoke() throws IOException {
    ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 1, 1L, "key", toJson());
    myListener.listen(record);

    verify(myInterceptor, times(1))
        .beforeConsume(any(ConsumerEvent.class));
    verify(myInterceptor, times(1))
        .afterSuccessConsume(any(ConsumerEvent.class));
  }

  @Test
  public void testError() throws IOException {
    sampleData.setName("error");

    try {
      ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 1, 1L, "key", toJson());
      myListener.listen(record);
      fail();
    } catch (Throwable ex) {
      verify(myInterceptor, times(1))
          .beforeConsume(any(ConsumerEvent.class));
      verify(myInterceptor, times(1))
          .afterFailedConsume(any(ConsumerEvent.class), any(NullPointerException.class));
    }
  }

  private String toJson() throws JsonProcessingException {
    return objectMapper.writeValueAsString(sampleData);
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public MyListener myListener(ObjectMapper objectMapper) {
      return new MyListener(objectMapper);
    }

  }

  @Data
  @Builder
  public static class SampleData {

    private String eventId;

    private String name;

  }

  public static class MyListener {

    private ObjectMapper objectMapper;

    public MyListener(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "sample")
    public void listen(ConsumerRecord<String, String> record) throws IOException {
      SampleData data = objectMapper.readValue(record.value(), SampleData.class);

      if ("error".equals(data.getName())) {
        throw new NullPointerException();
      }
    }

  }

  public static class MyInterceptor implements KafkaConsumerInterceptor {

  }
}