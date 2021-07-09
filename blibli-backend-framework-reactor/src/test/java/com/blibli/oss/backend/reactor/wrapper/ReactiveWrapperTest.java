package com.blibli.oss.backend.reactor.wrapper;

import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.blibli.oss.backend.reactor.scheduler.SchedulerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SchedulerTest.Application.class)
class ReactiveWrapperTest {

  @Autowired
  private SchedulerHelper schedulerHelper;

  @Autowired
  private ReactiveWrapperHelper reactiveWrapperHelper;

  private ReactiveWrapper reactiveWrapperRepository;

  private ReactiveWrapper reactiveWrapperNotFound;

  @BeforeEach
  void setUp() {
    reactiveWrapperRepository = reactiveWrapperHelper.of("REPOSITORY");
    reactiveWrapperNotFound = reactiveWrapperHelper.of("NOTFOUND");
  }

  @Test
  void testMono() {
    Mono.fromCallable(() -> "Eko")
      .doOnNext(value -> System.out.println(value + "FIRST:" + Thread.currentThread()))
      .flatMap(value -> reactiveWrapperRepository.mono(value::toUpperCase))
      .doOnNext(value -> System.out.println(value + "THIRD:" + Thread.currentThread()))
      .subscribeOn(schedulerHelper.of("NEW_SINGLE"))
      .block();
  }

  @Test
  void testFlux() {
    Flux.just("Eko", "Kurniawan", "Khannedy")
      .doOnNext(value -> System.out.println(value + "FIRST:" + Thread.currentThread()))
      .flatMap(value -> reactiveWrapperRepository.flux(() -> Stream.of(value.toUpperCase())))
      .doOnNext(value -> System.out.println(value + "THIRD:" + Thread.currentThread()))
      .subscribeOn(schedulerHelper.of("NEW_SINGLE"))
      .collectList()
      .block();
  }

  @Test
  void testFluxSchedulerNotFound() {
    Flux.just("Eko", "Kurniawan", "Khannedy")
      .doOnNext(value -> System.out.println(value + "FIRST:" + Thread.currentThread()))
      .flatMap(value -> reactiveWrapperNotFound.flux(() -> Stream.of(value.toUpperCase())))
      .doOnNext(value -> System.out.println(value + "THIRD:" + Thread.currentThread()))
      .subscribeOn(schedulerHelper.of("NOTFOUND"))
      .collectList()
      .block();
  }

}