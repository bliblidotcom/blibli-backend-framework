package com.blibli.oss.backend.kafka.repository;

import com.blibli.oss.backend.kafka.annotation.KafkaKey;
import com.blibli.oss.backend.kafka.annotation.KafkaTopic;
import com.blibli.oss.backend.kafka.producer.helper.KafkaHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KafkaRepositoryTest.Application.class)
@EmbeddedKafka(
  topics = KafkaRepositoryTest.TOPIC
)
@DirtiesContext
public class KafkaRepositoryTest {

  public static final String TOPIC = "KAFKA_REPOSITORY_TEST";

  @Autowired
  private EmbeddedKafkaBroker broker;

  private Consumer<String, String> consumer;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductKafkaRepository productKafkaRepository;

  @Autowired
  private CustomerKafkaRepository customerKafkaRepository;

  @BeforeEach
  void setUp() {
    consumer = KafkaHelper.newConsumer(broker);
    consumer.subscribe(Collections.singletonList(TOPIC));
  }

  @AfterEach
  void tearDown() {
    consumer.close();
  }

  @Test
  void testProductSuccess() throws JsonProcessingException {
    productKafkaRepository.sendAndSubscribe(Product.builder().id("id").name("name").build(), Schedulers.boundedElastic());
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    Assertions.assertEquals(record.key(), "id");
    Product product = objectMapper.readValue(record.value(), Product.class);
    Assertions.assertEquals(product.getId(), "id");
    Assertions.assertEquals(product.getName(), "name");

    productKafkaRepository.sendAndSubscribe(Product.builder().id("id").name("name").build(), Schedulers.boundedElastic());
    record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    Assertions.assertEquals(record.key(), "id");
    product = objectMapper.readValue(record.value(), Product.class);
    Assertions.assertEquals(product.getId(), "id");
    Assertions.assertEquals(product.getName(), "name");
  }

  @Test
  void testCustomerSuccess() throws JsonProcessingException {
    customerKafkaRepository.sendAndSubscribe(Customer.builder().id("id").name("name").build(), Schedulers.boundedElastic());
    ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    Assertions.assertEquals(record.key(), "id");
    Customer customer = objectMapper.readValue(record.value(), Customer.class);
    Assertions.assertEquals(customer.getId(), "id");
    Assertions.assertEquals(customer.getName(), "name");
  }

  @SpringBootApplication
  public static class Application {

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }

    @Bean
    public CustomerKafkaRepository customerKafkaRepository() {
      return new CustomerKafkaRepository();
    }

    @Bean
    public ProductKafkaRepository productKafkaRepository() {
      return new ProductKafkaRepository();
    }

  }

  public static class CustomerKafkaRepository extends AbstractKafkaRepository<Customer> implements KafkaRepository<Customer> {

  }

  public static class ProductKafkaRepository extends AbstractKafkaRepository<Product> implements KafkaRepository<Product> {

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @KafkaTopic(KafkaRepositoryTest.TOPIC)
  public static class Customer {

    @KafkaKey
    private String id;

    private String name;

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @KafkaTopic(KafkaRepositoryTest.TOPIC)
  public static class Product {

    @KafkaKey
    private String id;

    private String name;

  }
}
