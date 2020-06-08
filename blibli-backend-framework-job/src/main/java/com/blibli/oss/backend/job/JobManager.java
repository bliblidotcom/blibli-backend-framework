package com.blibli.oss.backend.job;

import reactor.core.publisher.Mono;

public interface JobManager {

  Mono<Void> execute(String command);

}
