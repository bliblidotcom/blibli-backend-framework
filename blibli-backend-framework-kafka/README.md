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

## Kafka Consumer Interceptor