package com.blibli.oss.backend.reactor;

import com.blibli.oss.backend.reactor.factory.ReactiveWrapperHelperFactoryBean;
import com.blibli.oss.backend.reactor.factory.SchedulerHelperFactoryBean;
import com.blibli.oss.backend.reactor.properties.ReactiveWrapperProperties;
import com.blibli.oss.backend.reactor.properties.SchedulerProperties;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  SchedulerProperties.class,
  ReactiveWrapperProperties.class
})
public class ReactorAutoConfiguration {

  @Bean
  public SchedulerHelperFactoryBean schedulerHelper(SchedulerProperties schedulerProperties) {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();
    factoryBean.setSchedulerProperties(schedulerProperties);
    return factoryBean;
  }

  @Bean
  public ReactiveWrapperHelperFactoryBean reactiveWrapperHelper(SchedulerHelper schedulerHelper,
                                                                ReactiveWrapperProperties reactiveWrapperProperties) {
    ReactiveWrapperHelperFactoryBean factoryBean = new ReactiveWrapperHelperFactoryBean();
    factoryBean.setSchedulerHelper(schedulerHelper);
    factoryBean.setProperties(reactiveWrapperProperties);
    return factoryBean;
  }

}
