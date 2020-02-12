package com.blibli.oss.scheduler.platform.configuration;

import com.blibli.oss.backend.kafka.producer.KafkaProducer;
import com.blibli.oss.scheduler.platform.repository.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(KafkaProducer.class)
public class SchedulerPlatformConfiguration {

  @Bean
  public SchedulerPlatformRepository schedulerPlatformRepository() {
    return new SchedulerPlatformRepository();
  }

}
