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

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.producer.PlainKafkaProducer;
import org.springframework.kafka.support.SendResult;
import rx.Single;

/**
 * @author Eko Kurniawan Khannedy
 */
public class KafkaProducerImpl implements KafkaProducer {

  private PlainKafkaProducer plainKafkaProducer;

  public KafkaProducerImpl(PlainKafkaProducer plainKafkaProducer) {
    this.plainKafkaProducer = plainKafkaProducer;
  }

  @Override
  public Single<SendResult<String, String>> send(String topic, String key, Object message, Integer partition, Long timestamp) {
    return Single.from(plainKafkaProducer.send(topic, key, message, partition, timestamp));
  }
}
