package com.blibli.oss.backend.apiclient.aop;

import com.blibli.oss.backend.apiclient.properties.ApiClientProperties;
import com.blibli.oss.backend.apiclient.properties.PropertiesHelper;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestMappingMetadataBuilder {

  private Map<String, Method> methods;

  private ApiClientProperties.ApiClientConfigProperties properties;

  private Map<String, MultiValueMap<String, String>> headers = new HashMap<>();

  private Map<String, Map<String, Integer>> queryParamPositions = new HashMap<>();

  private Map<String, Map<String, Integer>> headerParamPositions = new HashMap<>();

  private Map<String, Map<String, Integer>> cookieParamPositions = new HashMap<>();

  private Map<String, Map<String, Integer>> pathVariablePositions = new HashMap<>();

  private Map<String, RequestMethod> requestMethods = new HashMap<>();

  private Map<String, String> paths = new HashMap<>();

  private Map<String, Type> responseBodyClasses = new HashMap<>();

  private Map<String, String> contentTypes = new HashMap<>();

  private ApplicationContext applicationContext;

  private Class<?> type;

  private String name;

  public RequestMappingMetadataBuilder(ApplicationContext applicationContext,
                                       Class<?> type,
                                       String name) {
    this.applicationContext = applicationContext;
    this.type = type;
    this.name = name;
  }

  private void prepareProperties() {
    ApiClientProperties apiClientproperties = applicationContext.getBean(ApiClientProperties.class);
    properties = mergeApiClientConfigProperties(
      apiClientproperties.getConfigs().get(ApiClientProperties.DEFAULT),
      apiClientproperties.getConfigs().get(name)
    );
  }

  private ApiClientProperties.ApiClientConfigProperties mergeApiClientConfigProperties(ApiClientProperties.ApiClientConfigProperties defaultProperties,
                                                                                       ApiClientProperties.ApiClientConfigProperties properties) {
    ApiClientProperties.ApiClientConfigProperties configProperties = new ApiClientProperties.ApiClientConfigProperties();

    PropertiesHelper.copyConfigProperties(defaultProperties, configProperties);
    PropertiesHelper.copyConfigProperties(properties, configProperties);

    return configProperties;
  }

  private void prepareMethods() {
    methods = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(type))
      .filter(method -> method.getAnnotation(RequestMapping.class) != null)
      .collect(Collectors.toMap(Method::toString, method -> method));
  }

  private void preparePaths() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        String[] pathValues = requestMapping.path().length > 0 ? requestMapping.path() : requestMapping.value();
        if (pathValues.length > 0) {
          paths.put(methodName, pathValues[0]);
        } else {
          paths.put(methodName, "");
        }
      }
    });
  }

  private void prepareRequestMethods() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        RequestMethod[] methods = requestMapping.method();
        if (methods.length > 0) {
          requestMethods.put(methodName, methods[0]);
        } else {
          requestMethods.put(methodName, RequestMethod.GET);
        }
      }
    });
  }

  private void prepareResponseBodyClasses() {
    methods.forEach((methodName, method) -> {
      ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
      if (!parameterizedType.getRawType().getTypeName().equals(Mono.class.getName())) {
        throw new BeanCreationException(String.format("ApiClient method must return reactive, %s is not reactive", methodName));
      }

      Type[] typeArguments = parameterizedType.getActualTypeArguments();
      if (typeArguments.length != 1) {
        throw new BeanCreationException(String.format("ApiClient method must return 1 generic type, %s generic type is not 1", methodName));
      }

      responseBodyClasses.put(methodName, typeArguments[0]);
    });
  }

  private void prepareQueryParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> queryParamPosition = new HashMap<>();
        queryParamPositions.put(methodName, queryParamPosition);

        if (parameters.length > 0) {
          for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            if (annotation != null) {
              String name = StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                queryParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void prepareHeaderParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> headerParamPosition = new HashMap<>();
        headerParamPositions.put(methodName, headerParamPosition);

        if (parameters.length > 0) {
          for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RequestHeader annotation = parameter.getAnnotation(RequestHeader.class);
            if (annotation != null) {
              String name = StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                headerParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void preparePathVariables() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> pathVariablePosition = new HashMap<>();
        pathVariablePositions.put(methodName, pathVariablePosition);

        if (parameters.length > 0) {
          for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            PathVariable annotation = parameter.getAnnotation(PathVariable.class);
            if (annotation != null) {
              String name = StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                pathVariablePosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void prepareHeaders() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String[] consumes = requestMapping.consumes();
        if (consumes.length > 0) httpHeaders.addAll(HttpHeaders.CONTENT_TYPE, Arrays.asList(consumes));

        String[] produces = requestMapping.produces();
        if (produces.length > 0) httpHeaders.addAll(HttpHeaders.ACCEPT, Arrays.asList(produces));

        String[] requestHeaders = requestMapping.headers();
        for (String header : requestHeaders) {
          String[] split = header.split("=");
          if (split.length > 1) {
            httpHeaders.add(split[0], split[1]);
          } else {
            httpHeaders.add(split[0], "");
          }
        }
        headers.put(methodName, httpHeaders);
      }
    });
  }

  private void prepareCookieParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> cookieParamPosition = new HashMap<>();
        cookieParamPositions.put(methodName, cookieParamPosition);

        if (parameters.length > 0) {
          for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            CookieValue annotation = parameter.getAnnotation(CookieValue.class);
            if (annotation != null) {
              String name = StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                cookieParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void prepareContentTypes() {
    String defaultContentType = getDefaultContentType();
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        String[] consumes = requestMapping.consumes();
        if (consumes.length > 0) {
          contentTypes.put(methodName, consumes[0]);
        } else {
          contentTypes.put(methodName, defaultContentType);
        }
      }
    });
  }

  private String getDefaultContentType() {
    String defaultContentType = null;
    for (Map.Entry<String, String> entry : properties.getHeaders().entrySet()) {
      if (HttpHeaders.CONTENT_TYPE.equals(entry.getKey())) {
        defaultContentType = entry.getValue();
      }
    }
    return defaultContentType;
  }

  public RequestMappingMetadata build() {
    prepareProperties();
    prepareMethods();
    prepareHeaders();
    prepareQueryParams();
    prepareHeaderParams();
    preparePathVariables();
    prepareResponseBodyClasses();
    prepareRequestMethods();
    preparePaths();
    prepareCookieParams();
    prepareContentTypes();

    return RequestMappingMetadata.builder()
      .properties(properties)
      .methods(methods)
      .headerParamPositions(headerParamPositions)
      .headers(headers)
      .queryParamPositions(queryParamPositions)
      .pathVariablePositions(pathVariablePositions)
      .responseBodyClasses(responseBodyClasses)
      .requestMethods(requestMethods)
      .paths(paths)
      .cookieParamPositions(cookieParamPositions)
      .contentTypes(contentTypes)
      .build();
  }

}
