package com.blibli.oss.backend.kafka.interceptor.consumer;

import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.producer.helper.KafkaHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.scheduler.Schedulers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaConsumerInterceptorTest.Application.class)
@EmbeddedKafka(
  topics = KafkaConsumerInterceptorTest.TOPIC
)
@DirtiesContext
class KafkaConsumerInterceptorTest {

  public static final String TOPIC = "KafkaConsumerInterceptorTest";

  @Autowired
  private EmbeddedKafkaBroker broker;

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private HelloListener helloListener;

  @Autowired
  private HelloInterceptor helloInterceptor;

  private Consumer<String, String> consumer;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    broker.consumeFromEmbeddedTopics(consumer, TOPIC);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testListener() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.elastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloListener.key, "key");
    Assertions.assertEquals(helloListener.value, "value");

    helloInterceptor.reset();

    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.elastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloInterceptor.beforeConsume, "value");
    Assertions.assertEquals(helloInterceptor.afterSuccess, "value");
  }

  @Test
  void testInterceptorError() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "error", "value", Schedulers.elastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloInterceptor.beforeConsume, "value");
    Assertions.assertEquals(helloInterceptor.afterFailed, "value");
  }

  @Test
  void testInterceptorSkip() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "skip", "value", Schedulers.elastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertNotEquals(helloListener.value, "skip");
  }

  @SpringBootApplication
  public static class Application {


    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public HelloListener helloListener() {
      return new HelloListener();
    }

    @Bean
    public HelloInterceptor helloInterceptor() {
      return new HelloInterceptor();
    }

  }

  public static class HelloListener {

    @Getter
    private String key;

    @Getter
    private String value;

    @KafkaListener(topics = KafkaConsumerInterceptorTest.TOPIC, groupId = "custom-group")
    public void onMessage(ConsumerRecord<String, String> record) {
      if (record.key().equals("error")) {
        throw new RuntimeException("Error");
      } else {
        this.key = record.key();
        this.value = record.value();
      }
    }

  }

  @Slf4j
  public static class HelloInterceptor implements KafkaConsumerInterceptor {

    @Getter
    private String beforeConsume;

    @Getter
    private String afterSuccess;

    @Getter
    private String afterFailed;

    public void reset() {
      beforeConsume = null;
      afterSuccess = null;
      afterFailed = null;
    }

    @Override
    public boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
      log.info("BEFORE");
      this.beforeConsume = consumerRecord.value();
      return consumerRecord.key().equals("skip");
    }

    @Override
    public void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {
      log.info("AFTER");
      this.afterSuccess = consumerRecord.value();
    }

    @Override
    public void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {
      this.afterFailed = consumerRecord.value();
    }
  }

}