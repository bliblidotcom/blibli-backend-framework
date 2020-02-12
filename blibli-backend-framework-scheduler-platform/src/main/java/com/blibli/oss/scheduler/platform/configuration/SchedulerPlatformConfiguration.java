package com.blibli.oss.scheduler.platform.configuration;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.scheduler.platform.repository.DelayedJobKafkaRepository;
import com.blibli.oss.scheduler.platform.repository.ScheduledJobKafkaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(KafkaProducer.class)
public class SchedulerPlatformConfiguration {

  @Bean
  public DelayedJobKafkaRepository delayedJobKafkaRepository() {
    return new DelayedJobKafkaRepository();
  }

  @Bean
  public ScheduledJobKafkaRepository scheduledJobKafkaRepository() {
    return new ScheduledJobKafkaRepository();
  }

}
