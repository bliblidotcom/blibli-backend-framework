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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaProducerTest.Application.class)
@EmbeddedKafka(
  partitions = 1,
  topics = KafkaProducerTest.SAMPLE_TOPIC
)
class KafkaProducerTest {

  public static final String SAMPLE_TOPIC = "SAMPLE_TOPIC";

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private EmbeddedKafkaBroker broker;

  private Consumer<String, String> consumer;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    broker.consumeFromEmbeddedTopics(consumer, SAMPLE_TOPIC);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testSendSuccess() {
    kafkaProducer.send(SAMPLE_TOPIC, "key", "kafka value").subscribe();
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC);

    Assertions.assertEquals(record.key(), "key");
    Assertions.assertEquals(record.value(), "kafka value");
  }

  @Test
  void testSendLazy() {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      kafkaProducer.send(SAMPLE_TOPIC, "key", "kafka value"); // not subscribe it
      ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5_000L);
    });
  }

  @Test
  void testSendOn() {
    kafkaProducer.sendOn(SAMPLE_TOPIC, "key", "kafka value", Schedulers.elastic()).subscribe();
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC);

    Assertions.assertEquals(record.key(), "key");
    Assertions.assertEquals(record.value(), "kafka value");
  }

  @Test
  void testSendOnLazy() {
    Assertions.assertThrows(IllegalStateException.class, () -> {
      kafkaProducer.sendOn(SAMPLE_TOPIC, "key", "kafka value", Schedulers.elastic()); // not subscribe it
      ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC, 5_000L);
    });
  }

  @Test
  void testSendAndSubscribe() {
    kafkaProducer.sendAndSubscribe(SAMPLE_TOPIC, "key", "kafka value", Schedulers.elastic());
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, SAMPLE_TOPIC);

    Assertions.assertEquals(record.key(), "key");
    Assertions.assertEquals(record.value(), "kafka value");
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

  }

}