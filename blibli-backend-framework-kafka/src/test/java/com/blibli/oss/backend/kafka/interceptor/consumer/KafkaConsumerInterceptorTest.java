package com.blibli.oss.backend.kafka.interceptor.consumer;

import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.producer.helper.KafkaHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
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

import java.lang.reflect.Method;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaConsumerInterceptorTest.Application.class)
@EmbeddedKafka(
  partitions = 1,
  topics = {KafkaConsumerInterceptorTest.TOPIC, KafkaConsumerInterceptorTest.TOPIC_GOODBYE}
)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaConsumerInterceptorTest {

  public static final String TOPIC = "KafkaConsumerInterceptorTest";

  public static final String TOPIC_GOODBYE = "KafkaConsumerInterceptorTestGoodBye";

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private HelloListener helloListener;

  @Autowired
  private HelloInterceptor helloInterceptor;

  @Autowired
  private CounterInterceptor counterInterceptor;

  @Autowired
  private EmbeddedKafkaBroker broker;

  private Consumer<String, String> consumer;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    consumer.subscribe(Collections.singletonList(TOPIC));

    helloInterceptor.reset();
    counterInterceptor.reset();
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  @Order(4)
  void testListener() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.boundedElastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloListener.getKey(), "key");
    Assertions.assertEquals(helloListener.getValue(), "value");

    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.boundedElastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloInterceptor.beforeConsume, "value");
    Assertions.assertEquals(helloInterceptor.afterSuccess, "value");
    Assertions.assertEquals(0, counterInterceptor.getBeforeConsume());
    Assertions.assertEquals(0, counterInterceptor.getAfterSuccessConsume());
  }

  @Test
  @Order(3)
  void testListenerWithInterceptor() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC_GOODBYE, "key", "value", Schedulers.boundedElastic());
    Thread.sleep(4_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(1, counterInterceptor.getBeforeConsume());
    Assertions.assertEquals(1, counterInterceptor.getAfterSuccessConsume());
  }

  @Test
  @Order(2)
  void testInterceptorError() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "error", "value", Schedulers.boundedElastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertEquals(helloInterceptor.beforeConsume, "value");
    Assertions.assertEquals(helloInterceptor.afterFailed, "value");
    Assertions.assertEquals(0, counterInterceptor.getBeforeConsume());
    Assertions.assertEquals(0, counterInterceptor.getAfterSuccessConsume());
  }

  @Test
  @Order(1)
  void testInterceptorSkip() throws InterruptedException {
    kafkaProducer.sendAndSubscribe(TOPIC, "skip", "value", Schedulers.boundedElastic());
    Thread.sleep(2_000L); // wait 5 seconds until message received by listener

    Assertions.assertNotEquals(helloListener.value, "skip");
    Assertions.assertEquals(0, counterInterceptor.getBeforeConsume());
    Assertions.assertEquals(0, counterInterceptor.getAfterSuccessConsume());
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

    @Bean
    public GoodByeListener goodByeListener() {
      return new GoodByeListener();
    }

    @Bean
    public CounterInterceptor counterInterceptor() {
      return new CounterInterceptor();
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

  public static class GoodByeListener {

    @KafkaListener(topics = KafkaConsumerInterceptorTest.TOPIC_GOODBYE, groupId = "goodbye-group")
    public void onMessage(ConsumerRecord<String, String> record) {
      // do nothing
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

  public static class CounterInterceptor implements KafkaConsumerInterceptor {

    @Getter
    private Integer beforeConsume = 0;

    @Getter
    private Integer afterSuccessConsume = 0;

    @Getter
    private Integer afterFailedConsume = 0;

    public void reset() {
      beforeConsume = 0;
      afterFailedConsume = 0;
      afterSuccessConsume = 0;
    }

    @Override
    public boolean isSupport(Object bean, Method method) {
      return bean.getClass().isAssignableFrom(GoodByeListener.class);
    }

    @Override
    public boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
      beforeConsume++;
      return false;
    }

    @Override
    public void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {
      afterSuccessConsume++;
    }

    @Override
    public void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {
      afterFailedConsume++;
    }
  }

}