package com.blibli.oss.backend.command.configuration;

import com.blibli.oss.backend.command.cache.CommandCacheInterceptor;
import com.blibli.oss.backend.command.properties.CommandProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
@ConditionalOnProperty(value = "blibli.backend.command.cache.enabled", havingValue = "true")
public class CommandCacheAutoConfiguration {

  @Bean
  public CommandCacheInterceptor commandCacheInterceptor(ObjectMapper objectMapper,
                                                         ReactiveStringRedisTemplate redisTemplate,
                                                         CommandProperties commandProperties) {
    CommandCacheInterceptor interceptor = new CommandCacheInterceptor();
    interceptor.setObjectMapper(objectMapper);
    interceptor.setRedisTemplate(redisTemplate);
    interceptor.setCommandProperties(commandProperties);
    return interceptor;
  }

}
