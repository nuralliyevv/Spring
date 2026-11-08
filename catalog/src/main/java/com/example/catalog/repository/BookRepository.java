package com.example.catalog.repository;

import com.example.catalog.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Derived query: Spring builds the SQL from the method name
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    // Custom JPQL query with @Query
    @Query("select b from Book b where lower(b.title) like lower(concat('%', :q, '%'))")
    Page<Book> searchByTitle(@Param("q") String q, Pageable pageable);
}