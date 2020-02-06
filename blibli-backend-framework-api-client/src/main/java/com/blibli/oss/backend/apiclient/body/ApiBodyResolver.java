package com.blibli.oss.backend.apiclient.body;

import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;

import java.lang.reflect.Method;

public interface ApiBodyResolver {

  boolean canResolve(String contentType);

  BodyInserter<?, ? super ClientHttpRequest> resolve(Method method, Object[] arguments);

}
