package com.blibli.oss.backend.version;

import com.blibli.oss.backend.version.properties.VersionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("com.blibli.oss.backend.version.controller")
public class VersionAutoConfiguration {

}
