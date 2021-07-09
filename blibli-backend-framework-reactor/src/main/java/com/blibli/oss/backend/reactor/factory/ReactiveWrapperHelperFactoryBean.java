package com.blibli.oss.backend.reactor.factory;

import com.blibli.oss.backend.reactor.properties.ReactiveWrapperProperties;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.blibli.oss.backend.reactor.wrapper.ReactiveWrapper;
import com.blibli.oss.backend.reactor.wrapper.ReactiveWrapperHelper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ReactiveWrapperHelperFactoryBean implements FactoryBean<ReactiveWrapperHelper> {

  @Setter
  private SchedulerHelper schedulerHelper;

  @Setter
  private ReactiveWrapperProperties properties;

  @Override
  public ReactiveWrapperHelper getObject() throws Exception {
    Map<String, ReactiveWrapper> map = new HashMap<>();
    properties.getConfigs().forEach((key, item) -> {
      map.put(key, new ReactiveWrapperImpl(schedulerHelper, item.getSubscriber(), item.getPublisher()));
    });

    ReactiveWrapperImpl defaultReactiveWrapper = new ReactiveWrapperImpl(schedulerHelper, properties.getDefaultSubscriber(), properties.getDefaultPublisher());
    return new ReactiveWrapperHelperImpl(map, defaultReactiveWrapper);
  }

  @Override
  public Class<?> getObjectType() {
    return ReactiveWrapperHelper.class;
  }

  @AllArgsConstructor
  private static class ReactiveWrapperHelperImpl implements ReactiveWrapperHelper {

    private Map<String, ReactiveWrapper> map;

    private ReactiveWrapper defaultWrapper;

    @Override
    public ReactiveWrapper of(String name) {
      return map.getOrDefault(name, defaultWrapper);
    }
  }

  @AllArgsConstructor
  private static class ReactiveWrapperImpl implements ReactiveWrapper {

    private SchedulerHelper schedulerHelper;

    private String subscriber;

    private String publisher;

    @Override
    public <T> Mono<T> mono(Supplier<T> supplier) {
      return Mono.fromSupplier(supplier)
        .doOnNext(t -> System.out.println("THREAD " + Thread.currentThread()))
        .subscribeOn(schedulerHelper.of(subscriber))
        .publishOn(schedulerHelper.of(publisher));
    }

    @Override
    public <T> Flux<T> flux(Supplier<Stream<? extends T>> supplier) {
      return Flux.fromStream(supplier)
        .doOnNext(t -> System.out.println("THREAD " + Thread.currentThread()))
        .subscribeOn(schedulerHelper.of(subscriber))
        .publishOn(schedulerHelper.of(publisher));
    }
  }
}
