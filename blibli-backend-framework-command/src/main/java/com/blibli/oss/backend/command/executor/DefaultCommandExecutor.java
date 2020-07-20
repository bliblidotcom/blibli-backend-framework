package com.blibli.oss.backend.command.executor;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.command.interceptor.CommandInterceptor;
import com.blibli.oss.backend.command.interceptor.InterceptorUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Slf4j
public class DefaultCommandExecutor implements CommandExecutor, ApplicationContextAware, InitializingBean {

  @Setter
  private Validator validator;

  @Setter
  private ApplicationContext applicationContext;

  private List<CommandInterceptor> commandInterceptors;

  @Override
  public void afterPropertiesSet() {
    commandInterceptors = InterceptorUtil.getCommandInterceptors(applicationContext);
  }

  @Override
  public <R, T> Mono<T> execute(Class<? extends Command<R, T>> commandClass, R request) {
    return Mono.fromCallable(() -> applicationContext.getBean(commandClass))
      .doOnNext(command -> validateRequestIfNeeded(request, command))
      .flatMap(command -> doExecute(command, request));
  }

  private <R, T> void validateRequestIfNeeded(R request, Command<R, T> command) {
    if (command.validateRequest()) validateAndThrownIfInvalid(request);
  }

  private <R> void validateAndThrownIfInvalid(R request) throws ConstraintViolationException {
    Set<ConstraintViolation<R>> constraintViolations = validator.validate(request);
    if (!constraintViolations.isEmpty()) throw new ConstraintViolationException(constraintViolations);
  }

  private <R, T> Mono<T> doExecute(Command<R, T> command, R request) {
    return InterceptorUtil.fireBefore(commandInterceptors, command, request)
      .switchIfEmpty(doExecuteCommand(request, command));
  }

  private <R, T> Mono<T> doExecuteCommand(R request, Command<R, T> command) {
    return command.execute(request)
      .doOnSuccess(response -> InterceptorUtil.fireAfterSuccess(commandInterceptors, command, request, response).subscribe())
      .doOnError(throwable -> InterceptorUtil.fireAfterFailed(commandInterceptors, command, request, throwable).subscribe())
      .onErrorResume(throwable -> command.fallback(throwable, request));
  }
}
