package com.blibli.oss.backend.scheduler.platform.repository;

import com.blibli.oss.backend.scheduler.platform.constant.SchedulerPlatformTopics;
import com.blibli.oss.backend.scheduler.platform.helper.KafkaHelper;
import com.blibli.oss.backend.scheduler.platform.model.CancelDelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.model.CancelScheduledJobRequest;
import com.blibli.oss.backend.scheduler.platform.model.DelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.model.ScheduledJobRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SchedulerPlatformRepositoryTest.Application.class)
@EmbeddedKafka(
  topics = {
    SchedulerPlatformTopics.SAVE_SCHEDULED_JOB_EVENT,
    SchedulerPlatformTopics.SAVE_DELAYED_JOB_EVENT,
    SchedulerPlatformTopics.CANCEL_SCHEDULED_JOB_EVENT,
    SchedulerPlatformTopics.CANCEL_DELAYED_JOB_EVENT
  },
  partitions = 1
)
public class SchedulerPlatformRepositoryTest {

  @Autowired
  private SchedulerPlatformRepository schedulerPlatformRepository;

  @Autowired
  private EmbeddedKafkaBroker broker;

  @Autowired
  private ObjectMapper objectMapper;

  private Consumer<String, String> consumer;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testDelayedJob() throws JsonProcessingException {
    broker.consumeFromEmbeddedTopics(consumer, SchedulerPlatformTopics.SAVE_DELAYED_JOB_EVENT);

    DelayedJobRequest request = DelayedJobRequest.builder()
      .id("id")
      .name("name")
      .group("group")
      .topic("topic")
      .payload("payload")
      .notifyTimes(Collections.singletonList(10_000L))
      .build();

    schedulerPlatformRepository.send(request).block();

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SchedulerPlatformTopics.SAVE_DELAYED_JOB_EVENT);

    Assertions.assertEquals("id", record.key());
    Assertions.assertEquals(objectMapper.writeValueAsString(request), record.value());
  }

  @Test
  void testScheduledJob() throws JsonProcessingException {
    broker.consumeFromEmbeddedTopics(consumer, SchedulerPlatformTopics.SAVE_SCHEDULED_JOB_EVENT);

    ScheduledJobRequest request = ScheduledJobRequest.builder()
      .id("id")
      .name("name")
      .group("group")
      .topic("topic")
      .payload("payload")
      .schedule(ScheduledJobRequest.Schedule.builder()
        .dayOfWeeks(Collections.singletonList(DayOfWeek.FRIDAY))
        .hours(Collections.singletonList(1))
        .build())
      .build();

    schedulerPlatformRepository.send(request).block();

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SchedulerPlatformTopics.SAVE_SCHEDULED_JOB_EVENT);

    Assertions.assertEquals("id", record.key());
    Assertions.assertEquals(objectMapper.writeValueAsString(request), record.value());
  }

  @Test
  void testCancelDelayedJob() throws JsonProcessingException {
    broker.consumeFromEmbeddedTopics(consumer, SchedulerPlatformTopics.CANCEL_DELAYED_JOB_EVENT);

    CancelDelayedJobRequest request = CancelDelayedJobRequest.builder()
      .id("id")
      .name("name")
      .group("group")
      .build();

    schedulerPlatformRepository.send(request).block();

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SchedulerPlatformTopics.CANCEL_DELAYED_JOB_EVENT);

    Assertions.assertEquals("id", record.key());
    Assertions.assertEquals(objectMapper.writeValueAsString(request), record.value());
  }

  @Test
  void testCancelScheduledJob() throws JsonProcessingException {
    broker.consumeFromEmbeddedTopics(consumer, SchedulerPlatformTopics.CANCEL_SCHEDULED_JOB_EVENT);

    CancelScheduledJobRequest request = CancelScheduledJobRequest.builder()
      .id("id")
      .name("name")
      .group("group")
      .build();

    schedulerPlatformRepository.send(request).block();

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SchedulerPlatformTopics.CANCEL_SCHEDULED_JOB_EVENT);

    Assertions.assertEquals("id", record.key());
    Assertions.assertEquals(objectMapper.writeValueAsString(request), record.value());
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

  }
}
