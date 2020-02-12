package com.blibli.oss.scheduler.platform.model;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import com.blibli.oss.scheduler.platform.constant.SchedulerPlatformTopics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@KafkaTopic(SchedulerPlatformTopics.CANCEL_DELAYED_JOB_EVENT)
public class CancelDelayedJobRequest implements SchedulerPlatformModel {

  @KafkaKey
  private String id;

  private String name;

  private String group;
}
