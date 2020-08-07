package com.blibli.oss.backend.reactor.wrapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ReactiveWrapper {

  <T> Mono<T> mono(Supplier<T> supplier);

  <T> Flux<T> flux(Supplier<Stream<? extends T>> supplier);

}
