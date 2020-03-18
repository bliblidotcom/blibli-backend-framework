package com.blibli.oss.backend.scheduler.platform.constant;

public interface SchedulerPlatformTopics {

  String PREFIX = "scheduled_platform_";

  String SAVE_DELAYED_JOB_EVENT = PREFIX + "save_delayed_job_event";

  String CANCEL_DELAYED_JOB_EVENT = PREFIX + "cancel_delayed_job_event";

  String SAVE_SCHEDULED_JOB_EVENT = PREFIX + "save_scheduled_job_event";

  String CANCEL_SCHEDULED_JOB_EVENT = PREFIX + "cancel_scheduled_job_event";
}
