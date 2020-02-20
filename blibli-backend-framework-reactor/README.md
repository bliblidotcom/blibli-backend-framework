# Reactor Module

Reactor module is helper for Project Reactor. Project Reactor is reactive programming library used by Spring Web Flux.

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