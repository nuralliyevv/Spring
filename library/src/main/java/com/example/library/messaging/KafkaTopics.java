package com.example.library.messaging;

public final class KafkaTopics {

    public static final String BOOK_CREATED = "library.book-created";
    public static final String BOOK_CREATED_DLT = BOOK_CREATED + ".DLT";

    private KafkaTopics() {
    }
}