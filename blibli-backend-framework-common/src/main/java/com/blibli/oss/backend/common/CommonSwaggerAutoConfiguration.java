package com.blibli.oss.backend.common;

import com.blibli.oss.backend.common.properties.PagingProperties;
import com.blibli.oss.backend.common.swagger.PagingRequestSwaggerIgnoredParameter;
import com.blibli.oss.backend.swagger.SwaggerAutoConfiguration;
import com.blibli.oss.backend.swagger.api.SwaggerIgnoredParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SwaggerAutoConfiguration.class)
@AutoConfigureAfter(CommonAutoConfiguration.class)
public class CommonSwaggerAutoConfiguration {

  @Bean
  public SwaggerIgnoredParameter pagingRequestSwaggerIgnoredParameter() {
    return new PagingRequestSwaggerIgnoredParameter();
  }

  @Bean
  public Parameter queryPagingRequestPage(PagingProperties pagingProperties) {
    return new QueryParameter()
      .name(pagingProperties.getQuery().getPageKey())
      .example(pagingProperties.getDefaultPage())
      .required(true);
  }

  @Bean
  public Parameter queryPagingRequestItemPerPage(PagingProperties pagingProperties) {
    return new QueryParameter()
      .name(pagingProperties.getQuery().getItemPerPageKey())
      .example(pagingProperties.getDefaultItemPerPage())
      .required(true);
  }

  @Bean
  public Parameter queryPagingRequestSortBy(PagingProperties pagingProperties) {
    return new QueryParameter()
      .name(pagingProperties.getQuery().getSortByKey())
      .required(false);
  }

}
