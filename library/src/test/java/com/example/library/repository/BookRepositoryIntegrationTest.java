package com.example.library.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.TestcontainersConfiguration;
import com.example.library.entity.BookEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryIntegrationTest {

    @Autowired
    private BookRepository repository;

    private static BookEntity book(String title, String author, String isbn) {
        return new BookEntity(null, title, author, isbn, 2008);
    }

    @Test
    void save_generatesIdAndPersistsBook() {
        BookEntity saved = repository.save(book("Clean Code", "Robert Martin", "9780132350884"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(BookEntity::getTitle)
                .isEqualTo("Clean Code");
    }

    @Test
    void findByIsbn_existing_returnsBook() {
        repository.save(book("Clean Code", "Robert Martin", "9780132350884"));

        assertThat(repository.findByIsbn("9780132350884")).isPresent();
        assertThat(repository.findByIsbn("0000000000000")).isEmpty();
    }

    @Test
    void existsByIsbn_returnsTrueOnlyForExistingIsbn() {
        repository.save(book("Clean Code", "Robert Martin", "9780132350884"));

        assertThat(repository.existsByIsbn("9780132350884")).isTrue();
        assertThat(repository.existsByIsbn("0000000000000")).isFalse();
    }

    @Test
    void findByAuthorIgnoreCaseOrderById_ignoresCaseAndSortsById() {
        BookEntity first = repository.save(book("Clean Code", "Robert Martin", "9780132350884"));
        BookEntity second = repository.save(book("Clean Architecture", "Robert Martin", "9780134494166"));
        repository.save(book("Effective Java", "Joshua Bloch", "9780134685991"));

        List<BookEntity> result = repository.findByAuthorIgnoreCaseOrderById("robert martin");

        assertThat(result).extracting(BookEntity::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void save_duplicateIsbn_violatesUniqueConstraint() {
        repository.saveAndFlush(book("Clean Code", "Robert Martin", "9780132350884"));

        assertThatThrownBy(() ->
                repository.saveAndFlush(book("Another title", "Someone", "9780132350884")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}