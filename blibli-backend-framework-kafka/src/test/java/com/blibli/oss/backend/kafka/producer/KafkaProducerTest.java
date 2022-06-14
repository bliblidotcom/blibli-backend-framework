package com.blibli.oss.backend.kafka.producer;

import com.blibli.oss.backend.kafka.producer.helper.KafkaHelper;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaProducerTest.Application.class)
@EmbeddedKafka(
  partitions = 1,
  topics = KafkaProducerTest.SAMPLE_TOPIC
)
@DirtiesContext
class KafkaProducerTest {

  public static final String SAMPLE_TOPIC = "HELLO";

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private EmbeddedKafkaBroker broker;

  private Consumer<String, String> consumer;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    consumer.subscribe(Collections.singletonList(SAMPLE_TOPIC));
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testSendSuccess() {
    kafkaProducer.send(SAMPLE_TOPIC, "key 1", "kafka value 1").subscribe();
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5000L);

    Assertions.assertEquals(record.key(), "key 1");
    Assertions.assertEquals(record.value(), "kafka value 1");
  }

  @Test
  void testSendLazy() {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      kafkaProducer.send(SAMPLE_TOPIC, "key 2", "kafka value 2"); // not subscribe it
      ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5_000L);
    });
  }

  @Test
  void testSendOn() {
    kafkaProducer.sendOn(SAMPLE_TOPIC, "key 3", "kafka value 3", Schedulers.boundedElastic()).subscribe();
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5000L);

    Assertions.assertEquals(record.key(), "key 3");
    Assertions.assertEquals(record.value(), "kafka value 3");
  }

  @Test
  void testSendOnLazy() {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      kafkaProducer.sendOn(SAMPLE_TOPIC, "key 4", "kafka value 4", Schedulers.boundedElastic()); // not subscribe it
      ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5_000L);
    });
  }

  @Test
  void testSendAndSubscribe() {
    kafkaProducer.sendAndSubscribe(SAMPLE_TOPIC, "key 5", "kafka value 5", Schedulers.boundedElastic());
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5000L);

    Assertions.assertEquals(record.key(), "key 5");
    Assertions.assertEquals(record.value(), "kafka value 5");
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

  }

}