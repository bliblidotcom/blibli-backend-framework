package com.blibli.oss.backend.common;

import com.blibli.oss.backend.common.properties.PagingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties({
  PagingProperties.class
})
@PropertySource(
  ignoreResourceNotFound = true,
  value = "classpath:backend-common.properties"
)
public class CommonAutoConfiguration {

}
