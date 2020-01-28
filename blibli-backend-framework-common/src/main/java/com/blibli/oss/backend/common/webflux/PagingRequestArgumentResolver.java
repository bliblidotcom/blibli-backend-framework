package com.blibli.oss.backend.common.webflux;

import com.blibli.oss.backend.common.model.request.PagingRequest;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.properties.PagingProperties;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PagingRequestArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String SORT_BY_SEPARATOR = ":";
  public static final String SORT_BY_SPLITTER = ",";
  public static final String EMPTY_STRING = "";

  private final PagingProperties pagingProperties;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return PagingRequest.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    return Mono.fromSupplier(() -> fromServerHttpRequest(exchange.getRequest()));
  }

  private PagingRequest fromServerHttpRequest(ServerHttpRequest request) {
    PagingRequest paging = new PagingRequest();

    paging.setPage(getInteger(
      request.getQueryParams().getFirst(pagingProperties.getQuery().getPageKey()),
      pagingProperties.getDefaultPage()
    ));
    paging.setItemPerPage(getInteger(
      request.getQueryParams().getFirst(pagingProperties.getQuery().getItemPerPageKey()),
      pagingProperties.getDefaultItemPerPage()
    ));

    if (paging.getItemPerPage() > pagingProperties.getMaxItemPerPage()) {
      paging.setItemPerPage(pagingProperties.getMaxItemPerPage());
    }
    paging.setSortBy(getSortByList(
      request.getQueryParams().getFirst(pagingProperties.getQuery().getSortByKey()),
      pagingProperties
    ));

    return paging;
  }

  private List<SortBy> getSortByList(String value, PagingProperties pagingProperties) {
    if (StringUtils.isEmpty(value)) {
      return Collections.emptyList();
    } else {
      return toSortByList(value, pagingProperties);
    }
  }

  private Integer getInteger(String value, Integer defaultValue) {
    if (value == null) {
      return defaultValue;
    } else {
      return toInt(value, defaultValue);
    }
  }

  public List<SortBy> toSortByList(String request, PagingProperties pagingProperties) {
    return Arrays.stream(request.split(SORT_BY_SPLITTER))
      .map(s -> toSortBy(s, pagingProperties))
      .filter(Objects::nonNull)
      .filter(sortBy -> Objects.nonNull(sortBy.getPropertyName()))
      .collect(Collectors.toList());
  }


  public SortBy toSortBy(String request, PagingProperties pagingProperties) {
    String sort = request.trim();
    if (StringUtils.isEmpty(sort.replaceAll(SORT_BY_SEPARATOR, EMPTY_STRING)) || sort.startsWith(SORT_BY_SEPARATOR)) {
      return null;
    }

    String[] sortBy = sort.split(SORT_BY_SEPARATOR);

    return new SortBy(
      getAt(sortBy, 0, null),
      getAt(sortBy, 1, pagingProperties.getDefaultSortDirection())
    );
  }

  public String getAt(String[] strings, int index, String defaultValue) {
    return strings.length <= index ? defaultValue : strings[index];
  }

  public Integer toInt(String value, Integer defaultValue) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }
}
