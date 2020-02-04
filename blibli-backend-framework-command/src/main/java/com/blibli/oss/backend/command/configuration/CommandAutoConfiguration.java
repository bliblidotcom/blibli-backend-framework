package com.blibli.oss.backend.command.configuration;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.command.executor.DefaultCommandExecutor;
import com.blibli.oss.backend.command.properties.CommandProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

@Configuration
@EnableConfigurationProperties({
  CommandProperties.class
})
public class CommandAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CommandExecutor commandExecutor(Validator validator) {
    DefaultCommandExecutor commandExecutor = new DefaultCommandExecutor();
    commandExecutor.setValidator(validator);
    return commandExecutor;
  }

}
