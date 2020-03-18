package com.blibli.oss.backend.apiclient.properties;

import com.blibli.oss.backend.apiclient.customizer.ApiClientCodecCustomizer;
import com.blibli.oss.backend.apiclient.customizer.ApiClientWebClientCustomizer;
import com.blibli.oss.backend.apiclient.error.ApiErrorResolver;
import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("blibli.backend.apiclient")
public class ApiClientProperties {

  public static final String DEFAULT = "default";

  private Map<String, ApiClientConfigProperties> configs = new HashMap<>();

  private String packages;

  /**
   * Every time you add attributes, don't forget to update merge method with
   * default properties at {@link PropertiesHelper#copyConfigProperties(ApiClientConfigProperties, ApiClientConfigProperties)}
   *
   * @see PropertiesHelper#copyConfigProperties(ApiClientConfigProperties, ApiClientConfigProperties)
   * @see com.blibli.oss.backend.apiclient.aop.RequestMappingMetadataBuilder#mergeApiClientConfigProperties(ApiClientConfigProperties, ApiClientConfigProperties)
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ApiClientConfigProperties {

    private String url;

    private Class<?> fallback;

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration readTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration writeTimeout = Duration.ofMillis(2000L);

    private Map<String, String> headers = new HashMap<>();

    private List<Class<? extends ApiClientInterceptor>> interceptors = new ArrayList<>();

    private List<Class<? extends ApiClientWebClientCustomizer>> webClientCustomizers = new ArrayList<>();

    private List<Class<? extends ApiClientCodecCustomizer>> codecCustomizers = new ArrayList<>();

    private Class<? extends ApiErrorResolver> errorResolver;

  }

}
