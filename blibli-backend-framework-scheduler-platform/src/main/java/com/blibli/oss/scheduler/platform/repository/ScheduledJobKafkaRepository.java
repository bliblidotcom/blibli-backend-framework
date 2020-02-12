package com.blibli.oss.scheduler.platform.repository;

import com.blibli.oss.backend.kafka.repository.AbstractKafkaRepository;
import com.blibli.oss.backend.kafka.repository.KafkaRepository;
import com.blibli.oss.scheduler.platform.model.DelayedJobRequest;

public class ScheduledJobKafkaRepository extends AbstractKafkaRepository<DelayedJobRequest> implements KafkaRepository<DelayedJobRequest> {

}
