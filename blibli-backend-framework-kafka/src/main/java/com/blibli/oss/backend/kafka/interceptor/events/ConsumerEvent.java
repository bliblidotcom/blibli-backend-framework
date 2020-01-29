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

package com.blibli.oss.backend.kafka.interceptor.events;

import lombok.Builder;
import lombok.Data;

/**
 * @author Eko Kurniawan Khannedy
 */
@Data
@Builder
public class ConsumerEvent {

  private String topic;

  private Integer partition;

  private String eventId;

  private String key;

  private String value;

  private Long timestamp;
}
