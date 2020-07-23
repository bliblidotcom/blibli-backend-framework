package com.blibli.oss.backend.newrelic.configuration;

import com.blibli.oss.backend.newrelic.aspect.CommandAspect;
import com.blibli.oss.backend.newrelic.aspect.service.AspectModifyService;
import com.blibli.oss.backend.newrelic.aspect.service.impl.AspectModifyServiceImpl;
import com.blibli.oss.backend.newrelic.injector.NewRelicTokenInjectorFilter;
import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.NewRelic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@Import(CommandAspect.class)
public class NewRelicReactorInstrumentationAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public Agent newRelicAgent() {
    return NewRelic.getAgent();
  }

  @Bean
  public NewRelicTokenInjectorFilter newRelicFilter(Agent agent) {
    log.debug("Creating NewRelicTokenInjectorFilter bean");
    return new NewRelicTokenInjectorFilter(agent);
  }

  @Bean
  public AspectModifyService aspectModifyService() {
    return new AspectModifyServiceImpl();
  }
}
