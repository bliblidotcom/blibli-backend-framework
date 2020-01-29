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

package com.blibli.oss.backend.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Eko Kurniawan Khannedy
 */
@Data
@ConfigurationProperties("kafka.plugin")
public class KafkaProperties {

  private LogProperties log = new LogProperties();

  private ModelProperties model = new ModelProperties();

  @Data
  public static class LogProperties {

    private boolean beforeSend = true;

    private boolean beforeConsume = true;

    private boolean whenFailedSetEventId = true;

    private boolean whenFailedGetEventId = true;
  }

  @Data
  public static class ModelProperties {

    private String routing = "routingId";

    private String identity = "eventId";

    private String trace = "span";

  }

}
