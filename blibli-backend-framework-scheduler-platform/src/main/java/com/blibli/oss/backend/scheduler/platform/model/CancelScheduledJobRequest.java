package com.blibli.oss.backend.scheduler.platform.model;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import com.blibli.oss.backend.scheduler.platform.constant.SchedulerPlatformTopics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@KafkaTopic(SchedulerPlatformTopics.CANCEL_SCHEDULED_JOB_EVENT)
public class CancelScheduledJobRequest implements SchedulerPlatformModel {

  @KafkaKey
  private String id;

  private String name;

  private String group;
}
