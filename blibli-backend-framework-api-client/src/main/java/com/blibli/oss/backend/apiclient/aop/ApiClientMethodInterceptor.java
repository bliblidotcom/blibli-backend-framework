package com.blibli.oss.backend.apiclient.aop;

import com.blibli.oss.backend.apiclient.annotation.ApiClient;
import com.blibli.oss.backend.apiclient.aop.fallback.ApiClientFallback;
import com.blibli.oss.backend.apiclient.aop.fallback.FallbackMetadata;
import com.blibli.oss.backend.apiclient.aop.fallback.FallbackMetadataBuilder;
import com.blibli.oss.backend.apiclient.body.ApiBodyResolver;
import com.blibli.oss.backend.apiclient.customizer.ApiClientCodecCustomizer;
import com.blibli.oss.backend.apiclient.customizer.ApiClientTcpClientCustomizer;
import com.blibli.oss.backend.apiclient.customizer.ApiClientWebClientCustomizer;
import com.blibli.oss.backend.apiclient.error.ApiErrorResolver;
import com.blibli.oss.backend.apiclient.interceptor.ApiClientInterceptor;
import com.blibli.oss.backend.apiclient.interceptor.GlobalApiClientInterceptor;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ApiClientMethodInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

  @Setter
  private ApplicationContext applicationContext;

  @Setter
  private Class<?> type;

  @Setter
  private String name;

  @Setter
  private AnnotationMetadata annotationMetadata;

  private List<ApiBodyResolver> bodyResolvers;

  private WebClient webClient;

  private ApiClientFallback apiClientFallback;

  private RequestMappingMetadata metadata;

  private ApiErrorResolver errorResolver;

  private Scheduler scheduler;

  @Override
  public void afterPropertiesSet() throws Exception {
    prepareAttribute();
    prepareWebClient();
    prepareFallback();
    prepareBodyResolvers();
    prepareErrorResolver();
    prepareScheduler();
  }

  private void prepareAttribute() {
    metadata = new RequestMappingMetadataBuilder(applicationContext, type, name).build();
  }

  private void prepareWebClient() {
    WebClient.Builder builder = applicationContext.getBean(WebClient.Builder.class)
      .exchangeStrategies(getExchangeStrategies())
      .baseUrl(metadata.getProperties().getUrl())
      .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
      .defaultHeaders(httpHeaders -> metadata.getProperties().getHeaders().forEach(httpHeaders::add))
      .filters(exchangeFilterFunctions -> exchangeFilterFunctions.addAll(getApiClientInterceptors()));

    for (ApiClientWebClientCustomizer apiClientWebClientCustomizer : getApiClientWebClientCustomizers()) {
      apiClientWebClientCustomizer.customize(builder);
    }

    webClient = builder.build();
  }

  private TcpClient getTcpClient() {
    TcpClient tcpClient = TcpClient.create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) metadata.getProperties().getConnectTimeout().toMillis())
      .doOnConnected(connection -> connection
        .addHandlerLast(new ReadTimeoutHandler(metadata.getProperties().getReadTimeout().toMillis(), TimeUnit.MILLISECONDS))
        .addHandlerLast(new WriteTimeoutHandler(metadata.getProperties().getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS))
      );

    for (ApiClientTcpClientCustomizer customizer : getApiClientTcpClientCustomizers()) {
      tcpClient = customizer.customize(tcpClient);
    }

    return tcpClient;
  }

  private ExchangeStrategies getExchangeStrategies() {
    ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
    return ExchangeStrategies.builder().codecs(clientDefaultCodecsConfigurer -> {
      clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
      clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
      for (ApiClientCodecCustomizer apiClientCodecCustomizer : getApiClientCodecCustomizers()) {
        apiClientCodecCustomizer.customize(clientDefaultCodecsConfigurer);
      }
    }).build();
  }

  private Set<ApiClientCodecCustomizer> getApiClientCodecCustomizers() {
    Set<ApiClientCodecCustomizer> apiClientCodecCustomizers = new HashSet<>();
    metadata.getProperties().getCodecCustomizers().forEach(interceptorClass ->
      apiClientCodecCustomizers.add(applicationContext.getBean(interceptorClass))
    );

    ApiClient annotation = type.getAnnotation(ApiClient.class);
    for (Class<? extends ApiClientCodecCustomizer> interceptor : annotation.codecCustomizers()) {
      apiClientCodecCustomizers.add(applicationContext.getBean(interceptor));
    }
    return apiClientCodecCustomizers;
  }

  private Set<ApiClientWebClientCustomizer> getApiClientWebClientCustomizers() {
    Set<ApiClientWebClientCustomizer> apiClientWebClientCustomizers = new HashSet<>();
    metadata.getProperties().getWebClientCustomizers().forEach(interceptorClass ->
      apiClientWebClientCustomizers.add(applicationContext.getBean(interceptorClass))
    );

    ApiClient annotation = type.getAnnotation(ApiClient.class);
    for (Class<? extends ApiClientWebClientCustomizer> interceptor : annotation.webClientCustomizers()) {
      apiClientWebClientCustomizers.add(applicationContext.getBean(interceptor));
    }
    return apiClientWebClientCustomizers;
  }

  private Set<ApiClientTcpClientCustomizer> getApiClientTcpClientCustomizers() {
    Set<ApiClientTcpClientCustomizer> customizers = new HashSet<>();
    metadata.getProperties().getTcpClientCustomizers().forEach(interceptorClass ->
      customizers.add(applicationContext.getBean(interceptorClass))
    );

    ApiClient annotation = type.getAnnotation(ApiClient.class);
    for (Class<? extends ApiClientTcpClientCustomizer> customizer : annotation.tcpClientCustomizers()) {
      customizers.add(applicationContext.getBean(customizer));
    }
    return customizers;
  }

  private Set<ExchangeFilterFunction> getApiClientInterceptors() {
    Set<ExchangeFilterFunction> interceptors = new HashSet<>();
    metadata.getProperties().getInterceptors().forEach(interceptorClass ->
      interceptors.add(applicationContext.getBean(interceptorClass))
    );

    ApiClient annotation = type.getAnnotation(ApiClient.class);
    for (Class<? extends ApiClientInterceptor> interceptor : annotation.interceptors()) {
      interceptors.add(applicationContext.getBean(interceptor));
    }

    // Add GlobalApiClientInterceptor
    interceptors.addAll(applicationContext.getBeansOfType(GlobalApiClientInterceptor.class).values());
    return interceptors;
  }

  private void prepareFallback() {
    ApiClient annotation = type.getAnnotation(ApiClient.class);
    Object fallback = null;
    if (annotation.fallback() != Void.class) {
      fallback = applicationContext.getBean(annotation.fallback());
    }

    if (Objects.nonNull(metadata.getProperties().getFallback())) {
      fallback = applicationContext.getBean(metadata.getProperties().getFallback());
    }

    FallbackMetadata metadata = null;
    if (Objects.nonNull(fallback)) {
      metadata = new FallbackMetadataBuilder(type, fallback.getClass()).build();
    }

    apiClientFallback = ApiClientFallback.builder()
      .fallback(fallback)
      .metadata(metadata)
      .build();
  }

  private void prepareBodyResolvers() {
    bodyResolvers = new ArrayList<>(applicationContext.getBeansOfType(ApiBodyResolver.class).values());
  }

  private void prepareErrorResolver() {
    ApiClient annotation = type.getAnnotation(ApiClient.class);
    errorResolver = applicationContext.getBean(annotation.errorResolver());

    if (Objects.nonNull(metadata.getProperties().getErrorResolver())) {
      errorResolver = applicationContext.getBean(metadata.getProperties().getErrorResolver());
    }
  }

  private void prepareScheduler() {
    SchedulerHelper schedulerHelper = applicationContext.getBean(SchedulerHelper.class);
    ApiClient annotation = type.getAnnotation(ApiClient.class);
    if (schedulerHelper.of(annotation.name()) != Schedulers.immediate()) {
      scheduler = schedulerHelper.of(annotation.name());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    String methodName = method.toString();
    Object[] arguments = invocation.getArguments();

    Mono mono = Mono.fromCallable(() -> webClient)
      .map(client -> doMethod(methodName))
      .map(client -> getUriBuilder(methodName, arguments, client))
      .map(client -> doHeader(client, methodName, arguments))
      .map(client -> doBody(client, method, methodName, arguments))
      .flatMap(client -> doResponse(client, methodName))
      .onErrorResume(throwable -> doFallback((Throwable) throwable, method, arguments));

    if (Objects.nonNull(scheduler)) {
      return mono.subscribeOn(scheduler);
    } else {
      return mono;
    }
  }

  private WebClient.RequestHeadersUriSpec<?> doMethod(String methodName) {
    RequestMethod method = metadata.getRequestMethods().get(methodName);
    if (method.equals(RequestMethod.GET)) {
      return webClient.get();
    } else if (method.equals(RequestMethod.POST)) {
      return webClient.post();
    } else if (method.equals(RequestMethod.PUT)) {
      return webClient.put();
    } else if (method.equals(RequestMethod.PATCH)) {
      return webClient.patch();
    } else if (method.equals(RequestMethod.DELETE)) {
      return webClient.delete();
    } else if (method.equals(RequestMethod.OPTIONS)) {
      return webClient.options();
    } else if (method.equals(RequestMethod.HEAD)) {
      return webClient.head();
    } else {
      return webClient.get();
    }
  }

  private WebClient.RequestHeadersSpec<?> getUriBuilder(String methodName, Object[] arguments, WebClient.RequestHeadersUriSpec<?> client) {
    if (metadata.getApiUrlPositions().containsKey(methodName)) {
      String baseUrl = (String) arguments[metadata.getApiUrlPositions().get(methodName)];
      return client.uri(baseUrl, uriBuilder -> getUri(uriBuilder, methodName, arguments));
    } else {
      return client.uri(uriBuilder -> getUri(uriBuilder, methodName, arguments));
    }
  }

  private URI getUri(UriBuilder builder, String methodName, Object[] arguments) {
    builder.path(metadata.getPaths().get(methodName));

    metadata.getQueryParamPositions().get(methodName).forEach((paramName, position) -> {
      if (arguments[position] instanceof Collection) {
        Collection collection = (Collection) arguments[position];
        builder.queryParam(paramName, collection);
      } else {
        builder.queryParam(paramName, arguments[position]);
      }
    });

    metadata.getProperties().getParams().forEach(builder::queryParam);

    Map<String, Object> uriVariables = new HashMap<>();
    metadata.getPathVariablePositions().get(methodName).forEach((paramName, position) -> {
      uriVariables.put(paramName, arguments[position]);
    });

    return builder.build(uriVariables);
  }

  private WebClient.RequestHeadersSpec<?> doHeader(WebClient.RequestHeadersSpec<?> spec, String methodName, Object[] arguments) {
    metadata.getHeaders().get(methodName).forEach((key, values) -> {
      spec.headers(httpHeaders -> httpHeaders.addAll(key, values));
    });

    metadata.getHeaderParamPositions().get(methodName).forEach((key, position) -> {
      spec.headers(httpHeaders -> httpHeaders.add(key, String.valueOf(arguments[position])));
    });

    metadata.getCookieParamPositions().get(methodName).forEach((key, position) -> {
      spec.cookies(cookies -> cookies.add(key, String.valueOf(arguments[position])));
    });

    return spec;
  }

  private WebClient.RequestHeadersSpec<?> doBody(WebClient.RequestHeadersSpec<?> client, Method method, String methodName, Object[] arguments) {
    if (client instanceof WebClient.RequestBodySpec) {
      WebClient.RequestBodySpec bodySpec = (WebClient.RequestBodySpec) client;

      String contentType = metadata.getContentTypes().get(methodName);
      BodyInserter<?, ? super ClientHttpRequest> body = null;

      for (ApiBodyResolver bodyResolver : bodyResolvers) {
        if (bodyResolver.canResolve(contentType)) {
          body = bodyResolver.resolve(method, arguments);
        }
      }

      if (body != null) {
        return bodySpec.body((BodyInserter<?, ? super ClientHttpRequest>) body);
      }
    }
    return client;
  }

  @SuppressWarnings("unchecked")
  private Mono doResponse(WebClient.RequestHeadersSpec<?> client, String methodName) {
    Type type = metadata.getResponseBodyClasses().get(methodName);
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      if (ResponseEntity.class.equals(parameterizedType.getRawType())) {
        WebClient.ResponseSpec responseEntitySpec = client.retrieve().onStatus(HttpStatus::isError, clientResponse -> Mono.empty());

        if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
          ParameterizedType actualTypeArgument = (ParameterizedType) parameterizedType.getActualTypeArguments()[0];
          if (List.class.equals(actualTypeArgument.getRawType())) {
            return responseEntitySpec.toEntityList(ParameterizedTypeReference.forType(actualTypeArgument.getActualTypeArguments()[0]));
          }
        }

        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        if (Void.class.equals(actualTypeArgument)) {
          return responseEntitySpec.toBodilessEntity();
        } else {
          return responseEntitySpec.toEntity(ParameterizedTypeReference.forType(actualTypeArgument));
        }
      } else {
        return client.retrieve().bodyToMono(ParameterizedTypeReference.forType(parameterizedType));
      }
    } else {
      return client.retrieve().bodyToMono((Class) type);
    }
  }

  private Mono doFallback(Throwable throwable, Method method, Object[] arguments) {
    if (apiClientFallback.isAvailable()) {
      return errorResolver.resolve(throwable, type, method, arguments)
        .switchIfEmpty(apiClientFallback.invoke(method, arguments, throwable));
    } else {
      return errorResolver.resolve(throwable, type, method, arguments);
    }
  }
}
