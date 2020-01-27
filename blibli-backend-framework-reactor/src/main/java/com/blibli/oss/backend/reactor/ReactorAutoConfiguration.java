package com.blibli.oss.backend.reactor;

import com.blibli.oss.backend.reactor.factory.SchedulerHelperFactoryBean;
import com.blibli.oss.backend.reactor.properties.SchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  SchedulerProperties.class
})
public class ReactorAutoConfiguration {

  @Bean
  public SchedulerHelperFactoryBean schedulerHelper(SchedulerProperties schedulerProperties) {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();
    factoryBean.setSchedulerProperties(schedulerProperties);
    return factoryBean;
  }

}
