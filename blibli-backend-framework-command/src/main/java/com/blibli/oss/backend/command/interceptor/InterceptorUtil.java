package com.blibli.oss.backend.command.interceptor;

import com.blibli.oss.backend.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InterceptorUtil {

  public static List<CommandInterceptor> getCommandInterceptors(ApplicationContext applicationContext) {
    Map<String, CommandInterceptor> beans = applicationContext.getBeansOfType(CommandInterceptor.class);
    return beans.values().stream()
      .sorted(OrderComparator.INSTANCE)
      .collect(Collectors.toList());
  }

  public static <R, T> Mono<T> fireBefore(List<CommandInterceptor> commandInterceptors, Command<R, T> command, R request) {
    return Flux.merge(getListOfBeforeExecute(commandInterceptors, command, request))
      .filter(Objects::nonNull)
      .next();
  }

  private static <R, T> List<Mono<T>> getListOfBeforeExecute(List<CommandInterceptor> commandInterceptors, Command<R, T> command, R request) {
    return commandInterceptors.stream().map(commandInterceptor ->
      doBeforeExecuteWithFallback(command, request, commandInterceptor)
    ).collect(Collectors.toList());
  }

  private static <R, T> Mono<T> doBeforeExecuteWithFallback(Command<R, T> command, R request, CommandInterceptor commandInterceptor) {
    return commandInterceptor.before(command, request)
      .onErrorResume(throwable -> {
        log.warn("Error beforeExecute() interceptor " + commandInterceptor.getClass().getName(), throwable);
        return Mono.empty();
      });
  }

  public static <R, T> Mono<Long> fireAfterSuccess(List<CommandInterceptor> commandInterceptors, Command<R, T> command, R request, T response) {
    return Flux.merge(getListOfAfterSuccessExecute(commandInterceptors, command, request, response))
      .count();
  }

  private static <R, T> List<Mono<Void>> getListOfAfterSuccessExecute(Collection<CommandInterceptor> commandInterceptors, Command<R, T> command, R request, T response) {
    return commandInterceptors.stream().map(commandInterceptor ->
      doAfterSuccessExecuteWithFallback(command, request, response, commandInterceptor)
    ).collect(Collectors.toList());
  }

  private static <R, T> Mono<Void> doAfterSuccessExecuteWithFallback(Command<R, T> command, R request, T response, CommandInterceptor commandInterceptor) {
    return commandInterceptor.afterSuccess(command, request, response)
      .onErrorResume(throwable -> {
        log.warn("Error afterSuccessExecute() interceptor " + commandInterceptor.getClass().getName(), throwable);
        return Mono.empty();
      });
  }

  public static <R, T> Mono<Long> fireAfterFailed(List<CommandInterceptor> commandInterceptors, Command<R, T> command, R request, Throwable throwable) {
    return Flux.merge(getListOfAfterFailedExecute(commandInterceptors, command, request, throwable))
      .count();
  }

  private static <R, T> List<Mono<Void>> getListOfAfterFailedExecute(List<CommandInterceptor> commandInterceptors, Command<R, T> command, R request, Throwable throwable) {
    return commandInterceptors.stream().map(commandInterceptor ->
      doAfterFailedExecuteWithFallback(command, request, throwable, commandInterceptor)
    ).collect(Collectors.toList());
  }

  private static <R, T> Mono<Void> doAfterFailedExecuteWithFallback(Command<R, T> command, R request, Throwable throwable, CommandInterceptor commandInterceptor) {
    return commandInterceptor.afterFailed(command, request, throwable)
      .onErrorResume(error -> {
        log.warn("Error afterFailedExecute() interceptor " + commandInterceptor.getClass().getName(), error);
        return Mono.empty();
      });
  }

}
