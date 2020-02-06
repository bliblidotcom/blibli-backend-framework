package com.blibli.oss.backend.apiclient.aop;

import com.blibli.oss.backend.apiclient.properties.ApiClientProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestMappingMetadata {

  private Map<String, Method> methods;

  private Object fallback;

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

}
