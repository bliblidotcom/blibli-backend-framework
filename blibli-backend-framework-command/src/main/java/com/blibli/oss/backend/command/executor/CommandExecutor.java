package com.blibli.oss.backend.command.executor;

import com.blibli.oss.backend.command.Command;
import reactor.core.publisher.Mono;

public interface CommandExecutor {

  <R, T> Mono<T> execute(Class<? extends Command<R, T>> commandClass, R request);

}
