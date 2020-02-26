# Kafka Module

Kafka Module is library for kafka producer and consumer based on spring-kafka. 
Kafka Module support reactive programming using Project Reactor. 

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-kafka</artifactId>
</dependency>
```

## Reactive Kafka Producer

When we using spring-kafka, we can use `KafkaTemplate` to publish data to kafka, 
but in Kafka Module, we use `KafkaProducer` class to publish data to kafka.

All operation on `KafkaProducer` is reactive. It's return `Mono<T>`, 
so DON'T FORGET to subscribe it, if you forget, the data will not be sent to kafka.

```java
@Autowired
private KafkaProducer kafkaProducer;

// pubsih data to kafka 
Mono<SendResult<String, String>> result = kafkaProducer.send(topic, key, payload);
Mono<SendResult<String, String>> result = kafkaProducer.send(producerEvent);

// publish data to kafka on different scheduler 
Mono<SendResult<String, String>> result = kafkaProducer.sendOn(topic, key, payload, scheduler);
Mono<SendResult<String, String>> result = kafkaProducer.sendOn(producerEvent, scheduler);

// publish data to kafka on different scheduler and forget the result
kafkaProducer.sendAndSubscribe(topic, key, payload, scheduler);
kafkaProducer.sendAndSubscribe(producerEvent, scheduler);
```

## Kafka Repository

Kafka Module also create Repository Pattern for send data to kafka. Sometimes this is useful to simplify send domain data to kafka.

First, we need to create domain class for kafka object. It's simple POJO class. And to set kafka topic name, 
we can use `@KafkaTopic`, and to set kafka key, we can use `@KafkaKey`

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@KafkaTopic("customer_topic")
public class CustomerEvent {

  @KafkaKey
  private String id;

  private Gender gender;

  private String firstName;

  private String lastName;

  private String email;
}
```

And we can create kafka repository. Kafka module already create abstract class and interface to simplify this process.

```java
@Component
public class CustomerKafkaRepository extends AbstractKafkaRepository<CustomerEvent> implements KafkaRepository<CustomerEvent> {

}
``` 

Now we can send data to kafka using this repository object

```java
@Autowired
private CustomerKafkaRepository customerKafkaRepository;

Mono<SendResult<String, String>> result = customerKafkaRepository.send(customerEvent);
Mono<SendResult<String, String>> result = customerKafkaRepository.sendOn(customerEvent, scheduler);
customerKafkaRepository.sendAndSubscribe(customerEvent, scheduler);
```

## Kafka Producer Interceptor

Sometimes we want to do something before we send data to kafka. We can do it manually on our code. 
Or Kafka Module already create `KafkaProducerInterceptor`. We can create bean class of `KafkaProducerInterceptor`, and register it to spring.

```java
public interface KafkaProducerInterceptor {

  // this method will invoked before send data to kafka
  default ProducerEvent beforeSend(ProducerEvent event) {
    return event;
  }

}
```

## Kafka Consumer Interceptor

We can also add interceptor for kafka consumer using `KafkaConsumerInterceptor`. 

```java
public interface KafkaConsumerInterceptor {
  
  // invoked before kafka listener invoked. If return true, kafka module will stop process
  default boolean beforeConsume(ConsumerRecord<String, String> consumerRecord) {
    return false;
  }

  // invoked after kafka listener success consumed data
  default void afterSuccessConsume(ConsumerRecord<String, String> consumerRecord) {

  }

  // invoked only if kafka listener failed consumed data and throw an exception
  default void afterFailedConsume(ConsumerRecord<String, String> consumerRecord, Throwable throwable) {

  }

}
```

## Log Kafka Message

Kafka Module already has `LogKafkaProducerInterceptor` and `LogKafkaConsumerInterceptor`. These interceptors are for log payload.
By default, Kafka Module will not log any payload in producer and consumer interceptor. But we can make it enabed if we want using properties.

```properties
blibli.backend.kafka.logging.before-send=true
blibli.backend.kafka.logging.before-consume=true
blibli.backend.kafka.logging.after-success-consume=true
blibli.backend.kafka.logging.after-failed-consume=true
```