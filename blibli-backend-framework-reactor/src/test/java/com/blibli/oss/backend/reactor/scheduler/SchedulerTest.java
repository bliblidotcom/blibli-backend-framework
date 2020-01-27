package com.blibli.oss.backend.reactor.scheduler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SchedulerTest.Application.class)
public class SchedulerTest {

  @Autowired
  private SchedulerHelper schedulerHelper;

  @Test
  void testScheduler() {
    Assertions.assertNotNull(schedulerHelper.of("SINGLE"));
    Assertions.assertNotNull(schedulerHelper.of("PARALLEL"));
    Assertions.assertNotNull(schedulerHelper.of("IMMEDIATE"));
    Assertions.assertNotNull(schedulerHelper.of("ELASTIC"));
    Assertions.assertNotNull(schedulerHelper.of("NEW_ELASTIC"));
    Assertions.assertNotNull(schedulerHelper.of("NEW_SINGLE"));
    Assertions.assertNotNull(schedulerHelper.of("NEW_PARALLEL"));
    Assertions.assertNotNull(schedulerHelper.of("EXECUTOR_SINGLE_THREAD_POOL"));
    Assertions.assertNotNull(schedulerHelper.of("EXECUTOR_WORK_STEALING_POOL"));
    Assertions.assertNotNull(schedulerHelper.of("EXECUTOR_CACHE_THREAD_POOL"));
    Assertions.assertNotNull(schedulerHelper.of("EXECUTOR_FIXED_THREAD_POOL"));
    Assertions.assertNotNull(schedulerHelper.of("THREAD_POOL"));
  }

  @SpringBootApplication
  public static class Application {

  }
}
