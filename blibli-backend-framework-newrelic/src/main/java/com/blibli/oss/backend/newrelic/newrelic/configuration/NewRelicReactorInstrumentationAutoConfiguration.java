package com.blibli.oss.backend.newrelic.newrelic.configuration;

import com.blibli.oss.backend.newrelic.newrelic.aspect.CommandAspect;
import com.blibli.oss.backend.newrelic.newrelic.injector.NewRelicTokenInjectorFilter;
import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.NewRelic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass({EnableNewRelicReactorInstrumentation.class})
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
}
