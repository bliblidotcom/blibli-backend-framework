package com.blibli.oss.backend.version;

import com.blibli.oss.backend.version.controller.VersionController;
import com.blibli.oss.backend.version.properties.VersionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties({
  VersionProperties.class
})
@PropertySource(
  ignoreResourceNotFound = true,
  value = "classpath:version.properties"
)
public class VersionAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public VersionController versionController(@Autowired VersionProperties properties) {
    VersionController controller = new VersionController();
    controller.setProperties(properties);
    return controller;
  }

}
