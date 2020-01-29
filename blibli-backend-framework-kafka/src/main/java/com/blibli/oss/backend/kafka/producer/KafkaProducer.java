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

package com.blibli.oss.backend.kafka.producer;

import org.springframework.kafka.support.SendResult;
import rx.Single;

/**
 * @author Eko Kurniawan Khannedy
 */
public interface KafkaProducer {

  Single<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp);

  default Single<SendResult<String, String>> send(String topic, String key, Object message, Integer partition) {
    return send(topic, key, message, null, null);
  }

  default Single<SendResult<String, String>> send(String topic, String key, Object message) {
    return send(topic, key, message, null);
  }

  default Single<SendResult<String, String>> send(String topic, Object message) {
    return send(topic, null, message);
  }

}
