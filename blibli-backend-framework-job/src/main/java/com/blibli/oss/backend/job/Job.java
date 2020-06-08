package com.blibli.oss.backend.job;

import reactor.core.publisher.Mono;

public interface Job<T> {

  Mono<Void> execute(T request);

}
