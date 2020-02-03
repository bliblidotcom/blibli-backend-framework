package com.blibli.oss.backend.kafka.listener;

import com.blibli.oss.backend.kafka.interceptor.InterceptorUtil;
import com.blibli.oss.backend.kafka.interceptor.KafkaConsumerInterceptor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.adapter.BatchMessagingMessageListenerAdapter;
import org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.Collections;
import java.util.List;

public class KafkaListenerEndpoint extends MethodKafkaListenerEndpoint<String, String> {

  private KafkaListenerErrorHandler errorHandler;

  @Override
  public void setErrorHandler(KafkaListenerErrorHandler errorHandler) {
    super.setErrorHandler(errorHandler);
    this.errorHandler = errorHandler;
  }

  @Override
  protected MessagingMessageListenerAdapter<String, String> createMessageListenerInstance(MessageConverter messageConverter) {
    MessagingMessageListenerAdapter<String, String> listener;
    if (isBatchListener()) {
      BatchMessagingMessageListenerAdapter<String, String> messageListener = new BatchMessagingMessageListenerAdapter<>(getBean(), getMethod(), this.errorHandler);
      if (messageConverter instanceof BatchMessageConverter) {
        messageListener.setBatchMessageConverter((BatchMessageConverter) messageConverter);
      }
      listener = messageListener;
    } else {
      List<KafkaConsumerInterceptor> interceptors = Collections.emptyList();
      if (getBeanFactory() instanceof ListableBeanFactory) {
        ListableBeanFactory listableBeanFactory = (ListableBeanFactory) getBeanFactory();
        interceptors = InterceptorUtil.getConsumerInterceptors(listableBeanFactory);
      }
      KafkaMessageListener messageListener = new KafkaMessageListener(getBean(), getMethod(), this.errorHandler, interceptors);
      if (messageConverter instanceof RecordMessageConverter) {
        messageListener.setMessageConverter((RecordMessageConverter) messageConverter);
      }
      listener = messageListener;
    }
    if (getBeanResolver() != null) {
      listener.setBeanResolver(getBeanResolver());
    }
    return listener;
  }
}
