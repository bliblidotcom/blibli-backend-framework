package com.blibli.oss.backend.reactor.scheduler;

import reactor.core.scheduler.Scheduler;

public interface SchedulerHelper {

  Scheduler of(String name);

}
