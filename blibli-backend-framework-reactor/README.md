# Reactor Module

Reactor module is helper for Project Reactor. Project Reactor is reactive programming library used by Spring Web Flux.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-reactor</artifactId>
</dependency>
```

## Scheduler Helper

Reactor module can help to setup scheduler using configuration. And we can use `SchedulerHelper` class to get the object.

```properties

# scheduler configuration HELLO
blibli.backend.reactor.scheduler.configs.HELLO.type=elastic

# scheduler configuration TEST
blibli.backend.reactor.scheduler.configs.TEST.type=immediate

# scheduler configuration KAFKA
blibli.backend.reactor.scheduler.configs.KAFKA.type=thread_pool
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.core-pool-size=5
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.allow-core-thread-time-out=false
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.maximum-pool-size=100
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.queue-size=1000
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.queue-type=linked
blibli.backend.reactor.scheduler.configs.KAFKA.thread-pool.ttl=60s

# scheduler configuration COMMAND
blibli.backend.reactor.scheduler.configs.COMMAND.type=thread_pool
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.core-pool-size=5
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.allow-core-thread-time-out=false
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.maximum-pool-size=100
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.queue-size=1000
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.queue-type=linked
blibli.backend.reactor.scheduler.configs.COMMAND.thread-pool.ttl=60s

```

```java
@Autowired
private SchedulerHelper schedulerHelper;

@Override
public void afterPropertiesSet() throws Exception {
    Scheduler commandHello = schedulerHelper.of("HELLO");
    Scheduler commandTest = schedulerHelper.of("TEST");
    Scheduler commandKafka = schedulerHelper.of("KAFKA");
    Scheduler commandCommand = schedulerHelper.of("COMMAND");
}
```

## Example Complete Scheduler Properties

```properties
blibli.backend.reactor.scheduler.configs.SINGLE.type=single

blibli.backend.reactor.scheduler.configs.PARALLEL.type=parallel

blibli.backend.reactor.scheduler.configs.IMMEDIATE.type=immediate

blibli.backend.reactor.scheduler.configs.ELASTIC.type=elastic

blibli.backend.reactor.scheduler.configs.BOUNDED_ELASTICS.type=bounded_elastic

blibli.backend.reactor.scheduler.configs.NEW_ELASTIC.type=new_elastic
blibli.backend.reactor.scheduler.configs.NEW_ELASTIC.new-elastic.name=New Elastic
blibli.backend.reactor.scheduler.configs.NEW_ELASTIC.new-elastic.daemon=true
blibli.backend.reactor.scheduler.configs.NEW_ELASTIC.new-elastic.ttl=60s

blibli.backend.reactor.scheduler.configs.NEW_SINGLE.type=new_single
blibli.backend.reactor.scheduler.configs.NEW_SINGLE.new-single.name=New Single
blibli.backend.reactor.scheduler.configs.NEW_SINGLE.new-single.daemon=true

blibli.backend.reactor.scheduler.configs.NEW_PARALLEL.type=new_parallel
blibli.backend.reactor.scheduler.configs.NEW_PARALLEL.new-parallel.daemon=true
blibli.backend.reactor.scheduler.configs.NEW_PARALLEL.new-parallel.name=New Parallel
blibli.backend.reactor.scheduler.configs.NEW_PARALLEL.new-parallel.parallelism=4

blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.type=new_bounded_elastic
blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.thread-size=100
blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.queue-size=1000
blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.name=newBoundedElastic
blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.ttl=60s
blibli.backend.reactor.scheduler.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.daemon=true

blibli.backend.reactor.scheduler.configs.EXECUTOR_SINGLE_THREAD_POOL.type=executor
blibli.backend.reactor.scheduler.configs.EXECUTOR_SINGLE_THREAD_POOL.executor.type=single_thread_pool

blibli.backend.reactor.scheduler.configs.EXECUTOR_WORK_STEALING_POOL.type=executor
blibli.backend.reactor.scheduler.configs.EXECUTOR_WORK_STEALING_POOL.executor.type=work_stealing_pool
blibli.backend.reactor.scheduler.configs.EXECUTOR_WORK_STEALING_POOL.executor.parallelism=5

blibli.backend.reactor.scheduler.configs.EXECUTOR_CACHED_THREAD_POOL.type=executor
blibli.backend.reactor.scheduler.configs.EXECUTOR_CACHED_THREAD_POOL.executor.type=cached_thread_pool

blibli.backend.reactor.scheduler.configs.EXECUTOR_FIXED_THREAD_POOL.type=executor
blibli.backend.reactor.scheduler.configs.EXECUTOR_FIXED_THREAD_POOL.executor.type=fixed_thread_pool
blibli.backend.reactor.scheduler.configs.EXECUTOR_FIXED_THREAD_POOL.executor.number-of-thread=100

blibli.backend.reactor.scheduler.configs.THREAD_POOL.type=thread_pool
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.ttl=10s
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.core-pool-size=100
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.maximum-pool-size=1000
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.queue-type=linked
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.queue-size=100
blibli.backend.reactor.scheduler.configs.THREAD_POOL.thread-pool.allow-core-thread-time-out=true
```