package com.blibli.oss.backend.command.interceptor;

import com.blibli.oss.backend.command.Command;
import reactor.core.publisher.Mono;

public interface CommandInterceptor {

  default <R, T> Mono<T> before(Command<R, T> command, R request) {
    return Mono.empty();
  }

  default <R, T> Mono<Void> afterSuccess(Command<R, T> command, R request, T response) {
    return Mono.empty();
  }

  default <R, T> Mono<Void> afterFailed(Command<R, T> command, R request, Throwable throwable) {
    return Mono.empty();
  }
}
