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

import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.interceptor.events.ProducerEvent;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.Ordered;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Eko Kurniawan Khannedy
 */
@Slf4j
public class SleuthSpanProducerInterceptor implements KafkaProducerInterceptor {

  private static final String KAFKA_COMPONENT = "kafka:producer";

  private KafkaProperties.ModelProperties modelProperties;

  private Tracer tracer;

  public SleuthSpanProducerInterceptor(KafkaProperties.ModelProperties modelProperties, Tracer tracer) {
    this.modelProperties = modelProperties;
    this.tracer = tracer;
  }

  @Override
  public void beforeSend(ProducerEvent event) {
    PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(event.getValue().getClass(), modelProperties.getTrace());
    if (descriptor != null) {
      Method method = descriptor.getWriteMethod();
      if (method != null) {
        try {
          if (tracer.getCurrentSpan() == null) {
            String name = KAFKA_COMPONENT + ":" + event.getTopic();
            tracer.createSpan(name);
            log.debug("Sleuth span is not available, create new one");
          }

          Map<String, String> span = SleuthHelper.toMap(tracer.getCurrentSpan());
          method.invoke(event.getValue(), span);
          log.debug("Inject trace span {} to message", span);
        } catch (Throwable e) {
          log.error("Error while write span information", e);
        }
      }
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
