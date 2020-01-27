package com.blibli.oss.backend.common;

import com.blibli.oss.backend.common.properties.PagingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  PagingProperties.class
})
public class CommonAutoConfiguration {

}
