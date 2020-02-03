package com.blibli.oss.backend.kafka.interceptor;

import com.blibli.oss.backend.kafka.model.ProducerEvent;

public interface KafkaProducerInterceptor {

  ProducerEvent beforeSend(ProducerEvent event);

}
