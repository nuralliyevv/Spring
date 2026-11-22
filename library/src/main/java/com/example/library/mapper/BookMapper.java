package com.example.library.mapper;

import com.example.library.domain.Book;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // DTO -> domain (id is assigned later by the database)
    @Mapping(target = "id", ignore = true)
    Book toDomain(BookRequest request);

    // domain -> DTO
    BookResponse toResponse(Book book);

    // entity -> domain
    Book toDomain(BookEntity entity);

    // domain -> entity
    BookEntity toEntity(Book book);

    // copy new values from the domain object into an existing entity (keeps its id)
    @Mapping(target = "id", ignore = true)
    void updateEntity(Book book, @MappingTarget BookEntity entity);
}