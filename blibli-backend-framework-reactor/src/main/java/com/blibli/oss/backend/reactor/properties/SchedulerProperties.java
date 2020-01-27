package com.blibli.oss.backend.reactor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationProperties("blibli.backend.reactor.scheduler")
public class SchedulerProperties {

  private Map<String, SchedulerItemProperties> configs = new HashMap<>();

  @Data
  public static class SchedulerItemProperties {

    private SchedulerType type = SchedulerType.IMMEDIATE;

    private SchedulerNewElasticProperties newElastic = new SchedulerNewElasticProperties();

    private SchedulerNewParallelProperties newParallel = new SchedulerNewParallelProperties();

    private SchedulerNewSingleProperties newSingle = new SchedulerNewSingleProperties();

    private SchedulerExecutorProperties executor = new SchedulerExecutorProperties();

    private SchedulerThreadPoolProperties threadPool = new SchedulerThreadPoolProperties();

  }

  @Data
  public static class SchedulerNewElasticProperties {

    private String name;

    private Duration ttl = Duration.ofSeconds(60);

    private Boolean daemon = false;

  }

  @Data
  public static class SchedulerNewParallelProperties {

    private String name;

    private Integer parallelism = Optional.ofNullable(System.getProperty("reactor.schedulers.defaultPoolSize"))
      .map(Integer::parseInt)
      .orElseGet(() -> Runtime.getRuntime().availableProcessors());

    private Boolean daemon = false;
  }

  @Data
  public static class SchedulerNewSingleProperties {

    private String name;

    private Boolean daemon = false;

  }

  @Data
  public static class SchedulerExecutorProperties {

    private ExecutorType type = ExecutorType.CACHED_THREAD_POOL;

    private Integer numberOfThread;

    private Integer parallelism = Runtime.getRuntime().availableProcessors();

  }

  public enum ExecutorType {

    FIXED_THREAD_POOL,
    WORK_STEALING_POOL,
    SINGLE_THREAD_POOL,
    CACHED_THREAD_POOL

  }

  @Data
  public static class SchedulerThreadPoolProperties {

    private Integer corePoolSize = 10;

    private Boolean allowCoreThreadTimeOut = false;

    private Integer maximumPoolSize = 50;

    private Duration ttl = Duration.ofSeconds(60);

    private QueueType queueType = QueueType.LINKED;

    private Integer queueSize = Integer.MAX_VALUE;

  }

  public enum QueueType {

    ARRAY,
    LINKED

  }

  public enum SchedulerType {

    ELASTIC,
    PARALLEL,
    SINGLE,
    IMMEDIATE,
    NEW_ELASTIC,
    NEW_PARALLEL,
    NEW_SINGLE,
    EXECUTOR,
    THREAD_POOL

  }
}
