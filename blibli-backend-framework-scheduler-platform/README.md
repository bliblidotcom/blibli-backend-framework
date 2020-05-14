# Scheduler Platform Module

In Blibli, if we want to schedule or delayed a job. We can use Scheduler Platform. 
Scheduler Platform is event based using Apache Kafka. This module is client sdk for Scheduler Platform.

## Setup Dependency

```xml
<dependency>
  <groupId>com.blibli.oss</groupId>
  <artifactId>blibli-backend-framework-scheduler-platform</artifactId>
</dependency>
```

## Scheduler Platform Repository

This module is based on Kafka Repository. And we can use `SchedulerPlatformRepository` to send data to Scheduler Platform.

```java
@Autowired
private SchedulerPlatformRepository schedulerPlatformRepository;

DelayedJobRequest delayedJob = DelayedJobRequest.builder()
  .id("id")
  .name("name")
  .group("group")
  .topic("topic")
  .payload("payload")
  .notifyTimes(Collections.singletonList(10_000L))
  .build();

Mono<SendResult<String, String>> result = schedulerPlatformRepository.send(delayedJob);
Mono<SendResult<String, String>> result = schedulerPlatformRepository.sendOn(delayedJob, scheduler);
schedulerPlatformRepository.sendAndSubscribe(delayedJob, scheduler);

ScheduledJobRequest scheduledJob = ScheduledJobRequest.builder()
  .id("id")
  .name("name")
  .group("group")
  .topic("topic")
  .payload("payload")
  .schedule(ScheduledJobRequest.Schedule.builder()
    .dayOfWeeks(Collections.singletonList(DayOfWeek.FRIDAY))
    .hours(Collections.singletonList(1))
    .build())
  .build();

Mono<SendResult<String, String>> result = schedulerPlatformRepository.send(scheduledJob);
Mono<SendResult<String, String>> result = schedulerPlatformRepository.sendOn(scheduledJob, scheduler);
schedulerPlatformRepository.sendAndSubscribe(scheduledJob, scheduler);
```

We can also cancel the schedule using `SchedulerPlatformRepository`.

```java
CancelDelayedJobRequest cancelDelayedJob = CancelDelayedJobRequest.builder()
  .id("id")
  .name("name")
  .group("group")
  .build();

Mono<SendResult<String, String>> result = schedulerPlatformRepository.send(cancelDelayedJob);
Mono<SendResult<String, String>> result = schedulerPlatformRepository.sendOn(cancelDelayedJob, scheduler);
schedulerPlatformRepository.sendAndSubscribe(cancelDelayedJob, scheduler);

CancelScheduledJobRequest cancelScheduledJob = CancelScheduledJobRequest.builder()
  .id("id")
  .name("name")
  .group("group")
  .build();

Mono<SendResult<String, String>> result = schedulerPlatformRepository.send(cancelScheduledJob);
Mono<SendResult<String, String>> result = schedulerPlatformRepository.sendOn(cancelScheduledJob, scheduler);
schedulerPlatformRepository.sendAndSubscribe(cancelScheduledJob, scheduler);
```