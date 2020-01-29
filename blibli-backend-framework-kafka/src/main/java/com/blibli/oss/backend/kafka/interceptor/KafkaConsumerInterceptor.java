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
import org.springframework.core.Ordered;

/**
 * @author Eko Kurniawan Khannedy
 */
public interface KafkaConsumerInterceptor extends Ordered {

  /**
   * Invoked before consume message, if it return <code>true</code>, it will break the process.
   * If it return <code>false</code>, it will continue the process
   *
   * @param event consumer event
   * @return boolean
   */
  default boolean beforeConsume(ConsumerEvent event) {
    return false;
  }

  /**
   * Invoked after success consume message
   *
   * @param event consumer event
   */
  default void afterSuccessConsume(ConsumerEvent event) {
    // DO NOTHING
  }

  /**
   * Invoked after error consume message
   *
   * @param event     consumer event
   * @param throwable error exception
   */
  default void afterFailedConsume(ConsumerEvent event, Throwable throwable) {
    // DO NOTHING
  }

  /**
   * Get the order value of this object.
   *
   * @return default is 0
   */
  default int getOrder() {
    return 0;
  }

}
