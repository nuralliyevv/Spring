package com.example.library.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic bookCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.BOOK_CREATED).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic bookCreatedDeadLetterTopic() {
        return TopicBuilder.name(KafkaTopics.BOOK_CREATED_DLT).partitions(1).replicas(1).build();
    }

    /**
     * A failed message is retried 2 more times (1 second apart).
     * If it still fails, it is published to "<topic>.DLT".
     * Spring Boot applies this handler to all @KafkaListener containers.
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaOperations<Object, Object> kafkaOperations) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaOperations);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }
}