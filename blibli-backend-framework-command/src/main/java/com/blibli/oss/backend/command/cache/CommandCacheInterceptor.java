package com.blibli.oss.backend.command.cache;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.interceptor.CommandInterceptor;
import com.blibli.oss.backend.command.properties.CommandProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public class CommandCacheInterceptor implements CommandInterceptor, Ordered {

  @Getter
  private final int order = Ordered.HIGHEST_PRECEDENCE;

  @Setter
  private ReactiveStringRedisTemplate redisTemplate;

  @Setter
  private ObjectMapper objectMapper;

  @Setter
  private CommandProperties commandProperties;

  @Override
  public <R, T> Mono<T> before(Command<R, T> command, R request) {
    return Mono.fromCallable(() -> command.cacheKey(request))
      .flatMap(this::redisGet)
      .map(json -> readJson(command, json));
  }

  @Override
  public <R, T> Mono<Void> afterSuccess(Command<R, T> command, R request, T response) {
    return Mono.zip(
      evictCommandResponse(command, request),
      cacheCommandResponse(command, request, response)
    ).flatMap(objects -> Mono.empty());
  }

  private <R, T> Mono<Boolean> cacheCommandResponse(Command<R, T> command, R request, T response) {
    return Mono.zip(
      Mono.fromCallable(() -> command.cacheKey(request)),
      Mono.fromCallable(() -> writeJson(response))
    ).flatMap(this::redisSet)
      .switchIfEmpty(Mono.just(false));
  }

  private <R, T> Mono<Long> evictCommandResponse(Command<R, T> command, R request) {
    return Mono.fromCallable(() -> command.evictKeys(request))
      .map(keys -> keys.toArray(new String[0]))
      .flatMap(this::redisDelete)
      .switchIfEmpty(Mono.just(0L));
  }

  private Mono<String> redisGet(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  private Mono<Boolean> redisSet(Tuple2<String, String> tuple) {
    return redisTemplate.opsForValue().set(tuple.getT1(), tuple.getT2(), commandProperties.getCache().getTimeout());
  }

  private Mono<Long> redisDelete(String[] keys) {
    return redisTemplate.delete(Flux.fromArray(keys));
  }

  @SneakyThrows
  private <T> String writeJson(T response) {
    return objectMapper.writeValueAsString(response);
  }

  @SneakyThrows
  private <R, T> T readJson(Command<R, T> command, String json) {
    return objectMapper.readValue(json, command.responseClass());
  }
}
