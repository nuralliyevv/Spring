package com.example.library.repository;

import com.example.library.entity.BookEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<BookEntity> findByAuthorIgnoreCaseOrderById(String author);
}