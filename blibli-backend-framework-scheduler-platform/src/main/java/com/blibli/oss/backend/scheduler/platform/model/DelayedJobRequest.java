package com.blibli.oss.backend.scheduler.platform.model;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import com.blibli.oss.backend.scheduler.platform.constant.SchedulerPlatformTopics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@KafkaTopic(SchedulerPlatformTopics.SAVE_DELAYED_JOB_EVENT)
public class DelayedJobRequest implements SchedulerPlatformModel {

  @KafkaKey
  private String id;

  private String name;

  private String group;

  private String payload;

  private String topic;

  @Builder.Default
  private List<Long> notifyTimes = new ArrayList<>();
}
