package com.blibli.oss.backend.mandatoryparameter.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

public class ServerHelper {

  public static String getValueFromQueryOrHeader(ServerWebExchange exchange, String headerKey, String queryKey) {
    return getValueFromQueryOrHeader(exchange.getRequest(), headerKey, queryKey);
  }

  public static String getValueFromQueryOrHeader(ServerHttpRequest httpRequest, String headerKey, String queryKey) {
    return getValueFromQueryOrHeader(httpRequest.getHeaders(), httpRequest.getQueryParams(), headerKey, queryKey);
  }

  public static String getValueFromQueryOrHeader(HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams, String headerKey, String queryParamKey) {
    if (StringUtils.isEmpty(httpHeaders.getFirst(headerKey))) {
      return queryParams.getFirst(queryParamKey);
    } else {
      return httpHeaders.getFirst(headerKey);
    }
  }

}
