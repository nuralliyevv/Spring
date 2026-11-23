package com.example.library.messaging;

import com.example.library.event.BookCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookEventProducer {

    private static final Logger log = LoggerFactory.getLogger(BookEventProducer.class);

    private final KafkaTemplate<String, BookCreatedEvent> kafkaTemplate;

    public BookEventProducer(KafkaTemplate<String, BookCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookCreated(BookCreatedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.BOOK_CREATED, String.valueOf(event.bookId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish BookCreatedEvent for book {}", event.bookId(), ex);
                        } else {
                            log.info("Published BookCreatedEvent for book {} to {}", event.bookId(),
                                    result.getRecordMetadata().topic());
                        }
                    });
        } catch (Exception ex) {
            // the book is already saved, so a Kafka problem must not break the HTTP request
            log.error("Could not send BookCreatedEvent for book {}", event.bookId(), ex);
        }
    }
}