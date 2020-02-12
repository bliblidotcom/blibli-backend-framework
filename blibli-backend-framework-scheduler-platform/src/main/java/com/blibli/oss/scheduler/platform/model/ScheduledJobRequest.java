package com.blibli.oss.scheduler.platform.model;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import com.blibli.oss.scheduler.platform.constant.SchedulerPlatformTopics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@KafkaTopic(SchedulerPlatformTopics.SAVE_SCHEDULED_JOB_EVENT)
public class ScheduledJobRequest implements SchedulerPlatformModel {

  @KafkaKey
  private String id;

  private String name;

  private String group;

  private String payload;

  private String topic;

  @Builder.Default
  private Schedule schedule = new Schedule();

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Schedule {

    @Builder.Default
    private List<Integer> seconds = new ArrayList<>();

    @Builder.Default
    private List<Integer> minutes = new ArrayList<>();

    @Builder.Default
    private List<Integer> hours = new ArrayList<>();

    @Builder.Default
    private List<DayOfWeek> dayOfWeeks = new ArrayList<>();

    @Builder.Default
    private List<Integer> dayOfMonths = new ArrayList<>();

    @Builder.Default
    private List<Month> months = new ArrayList<>();

    @Builder.Default
    private List<Integer> years = new ArrayList<>();

  }
}
