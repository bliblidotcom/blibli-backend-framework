package com.blibli.oss.backend.reactor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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

    private SchedulerNewBoundedElasticProperties newBoundedElastic = new SchedulerNewBoundedElasticProperties();

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

    private Integer parallelism = Schedulers.DEFAULT_POOL_SIZE;

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

  @Data
  public static class SchedulerNewBoundedElasticProperties {

    private Integer threadSize = Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE;

    private Integer queueSize = Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE;

    private String name = "boundedElastic";

    private Duration ttl = Duration.ofSeconds(60);

    private Boolean daemon = Boolean.TRUE;

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
    THREAD_POOL,
    BOUNDED_ELASTIC,
    NEW_BOUNDED_ELASTIC

  }
}
