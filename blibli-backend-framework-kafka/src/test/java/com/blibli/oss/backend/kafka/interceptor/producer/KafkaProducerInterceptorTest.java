package com.blibli.oss.backend.kafka.interceptor.producer;

import com.blibli.oss.backend.kafka.interceptor.KafkaProducerInterceptor;
import com.blibli.oss.backend.kafka.model.ProducerEvent;
import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.backend.kafka.producer.helper.KafkaHelper;
import com.blibli.oss.backend.kafka.properties.KafkaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@SpringBootTest(classes = KafkaProducerInterceptorTest.Application.class)
@EmbeddedKafka(
  topics = KafkaProducerInterceptorTest.TOPIC
)
@DirtiesContext
class KafkaProducerInterceptorTest {

  public static final String TOPIC = "KafkaProducerInterceptorTest";

  @Autowired
  private EmbeddedKafkaBroker broker;

  @Autowired
  private KafkaProducer kafkaProducer;

  private Consumer<String, String> consumer;

  @Autowired
  private ErrorFlag errorFlag;

  @Autowired
  private KafkaProperties kafkaProperties;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    consumer.subscribe(Collections.singletonList(TOPIC));

    kafkaProperties.getLogging().setBeforeConsumeExcludeEvent(false);
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testInterceptor(CapturedOutput output) {
    errorFlag.setError(false);
    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.boundedElastic());

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    Assertions.assertEquals(record.key(), "key");
    Assertions.assertEquals(record.value(), "value changed");

    assertTrue(
      output.getOut()
        .contains("Send message to kafka : " +
          "ProducerEvent(topic=KafkaProducerInterceptorTest, partition=null, headers=null, key=key, value=value, timestamp=null)")
    );
  }

  @Test
  void testInterceptorExcludeEventOnLogs(CapturedOutput output) {
    kafkaProperties.getLogging().setBeforeSendExcludeEvent(true);

    errorFlag.setError(false);
    kafkaProducer.sendAndSubscribe(TOPIC, "key", "value", Schedulers.boundedElastic());

    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    Assertions.assertEquals(record.key(), "key");
    Assertions.assertEquals(record.value(), "value changed");

    assertTrue(
      output.getOut()
        .contains("Send message to kafka : " +
          "{topic: KafkaProducerInterceptorTest, key: key}")
    );
  }

  @Test
  void testInterceptorError() {
    errorFlag.setError(true);
    assertThrows(IllegalStateException.class, () -> {
      StepVerifier.create(kafkaProducer.send(TOPIC, "key", "value"))
        .expectError(RuntimeException.class);
      ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC, 5_000L);
    });
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public ChangeBodyInterceptor changeBodyInterceptor(ErrorFlag errorFlag) {
      return new ChangeBodyInterceptor(errorFlag);
    }

    @Bean
    public ErrorFlag errorFlag() {
      return new ErrorFlag();
    }

  }

  @AllArgsConstructor
  public static class ChangeBodyInterceptor implements KafkaProducerInterceptor {

    private ErrorFlag errorFlag;

    @Override
    public ProducerEvent beforeSend(ProducerEvent event) {
      if (errorFlag.isError) {
        throw new RuntimeException("Ups error");
      } else {
        event.setValue(event.getValue() + " changed");
        return event;
      }
    }
  }

  @Data
  public static class ErrorFlag {

    private boolean isError;

  }

}