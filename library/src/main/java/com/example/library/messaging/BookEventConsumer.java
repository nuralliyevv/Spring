package com.example.library.messaging;

import com.example.library.event.BookCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class BookEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookEventConsumer.class);

    /** Demo hook: a title starting with this prefix makes processing fail, to show the dead-letter topic. */
    static final String FAILURE_TITLE_PREFIX = "FAIL";

    @KafkaListener(topics = KafkaTopics.BOOK_CREATED, groupId = "library-service")
    public void onBookCreated(BookCreatedEvent event) {
        log.info("Received BookCreatedEvent: bookId={}, title='{}'", event.bookId(), event.title());

        if (event.title() != null && event.title().startsWith(FAILURE_TITLE_PREFIX)) {
            throw new IllegalStateException("Simulated processing failure for book " + event.bookId());
        }
        log.info("Processed BookCreatedEvent for book {}", event.bookId());
    }

    @KafkaListener(topics = KafkaTopics.BOOK_CREATED_DLT, groupId = "library-dlt")
    public void onDeadLetter(BookCreatedEvent event,
                             @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String error) {
        log.error("DEAD LETTER received for book {}: {}", event.bookId(), error);
    }
}