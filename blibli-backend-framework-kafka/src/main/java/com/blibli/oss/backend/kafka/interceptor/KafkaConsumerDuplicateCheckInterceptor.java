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

/**
 * @author Eko Kurniawan Khannedy
 */
public interface KafkaConsumerDuplicateCheckInterceptor extends KafkaConsumerInterceptor {

  @Override
  default boolean beforeConsume(ConsumerEvent event) {
    return event.getEventId() != null && isAlreadyConsumed(event.getEventId());
  }

  /**
   * Check is already consumed
   *
   * @param eventId event id
   * @return true if already consumed, false if not
   */
  boolean isAlreadyConsumed(String eventId);

  @Override
  default void afterSuccessConsume(ConsumerEvent event) {
    if (event.getEventId() != null) {
      markAsAlreadyConsumed(event.getEventId());
    }
  }

  /**
   * Mark event id as already consumed
   *
   * @param eventId event id
   */
  void markAsAlreadyConsumed(String eventId);
}
