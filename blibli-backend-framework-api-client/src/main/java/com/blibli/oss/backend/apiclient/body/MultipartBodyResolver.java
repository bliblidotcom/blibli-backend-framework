package com.blibli.oss.backend.apiclient.body;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MultipartBodyResolver implements ApiBodyResolver {

  @Override
  public boolean canResolve(String contentType) {
    return MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType);
  }

  @Override
  public BodyInserter<?, ? super ClientHttpRequest> resolve(Method method, Object[] arguments) {
    Parameter[] parameters = method.getParameters();
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      RequestPart annotation = parameter.getAnnotation(RequestPart.class);
      if (annotation != null) {
        String name = StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
        builder.part(name, arguments[i]);
      }
    }
    MultiValueMap<String, HttpEntity<?>> multiValueMap = builder.build();

    if (!multiValueMap.isEmpty()) {
      return BodyInserters.fromMultipartData(multiValueMap);
    }
    return null;
  }
}
