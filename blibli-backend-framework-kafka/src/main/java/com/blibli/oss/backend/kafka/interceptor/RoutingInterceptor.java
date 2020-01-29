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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Eko Kurniawan Khannedy
 */
@Slf4j
public class RoutingInterceptor implements KafkaProducerInterceptor {

  private KafkaProperties.ModelProperties modelProperties;

  public RoutingInterceptor(KafkaProperties.ModelProperties modelProperties) {
    this.modelProperties = modelProperties;
  }

  @Override
  public void beforeSend(ProducerEvent event) {
    PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(event.getValue().getClass(), modelProperties.getRouting());
    if (descriptor != null) {
      setKeyFromRoutingProperty(event, descriptor);
    }
  }

  private void setKeyFromRoutingProperty(ProducerEvent event, PropertyDescriptor descriptor) {
    Method method = descriptor.getReadMethod();
    if (method != null) {
      try {
        String routingId = (String) method.invoke(event.getValue());
        if (routingId == null) {
          routingId = UUID.randomUUID().toString();
        }
        event.setKey(routingId);
        log.debug("Inject routing id {} to message", routingId);
      } catch (IllegalAccessException | InvocationTargetException e) {
        log.error("Error while get routing id", e);
      }
    }
  }
}
