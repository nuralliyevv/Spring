package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.domain.Book;
import com.example.library.entity.BookEntity;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService(repository, Mappers.getMapper(BookMapper.class));
    }

    private static BookEntity entity(Long id, String isbn) {
        return new BookEntity(id, "Clean Code", "Robert Martin", isbn, 2008);
    }

    // ---------- create ----------

    @Test
    void create_newIsbn_savesBookAndReturnsItWithId() {
        Book input = new Book(null, "Clean Code", "Robert Martin", "9780132350884", 2008);
        when(repository.existsByIsbn("9780132350884")).thenReturn(false);
        when(repository.save(any(BookEntity.class))).thenAnswer(invocation -> {
            BookEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Book result = service.create(input);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Clean Code");

        ArgumentCaptor<BookEntity> captor = ArgumentCaptor.forClass(BookEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getIsbn()).isEqualTo("9780132350884");
        assertThat(captor.getValue().getAuthor()).isEqualTo("Robert Martin");
    }

    @Test
    void create_existingIsbn_throwsDuplicateAndDoesNotSave() {
        Book input = new Book(null, "Clean Code", "Robert Martin", "9780132350884", 2008);
        when(repository.existsByIsbn("9780132350884")).thenReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any());
    }

    // ---------- read ----------

    @Test
    void findById_existing_returnsBook() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity(1L, "9780132350884")));

        Book result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.isbn()).isEqualTo("9780132350884");
    }

    @Test
    void findById_missing_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_withoutAuthor_returnsAllBooks() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(
                entity(1L, "9780132350884"),
                entity(2L, "9780134685991")));

        List<Book> result = service.findAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_withAuthor_usesAuthorQuery() {
        when(repository.findByAuthorIgnoreCaseOrderById("Robert Martin"))
                .thenReturn(List.of(entity(1L, "9780132350884")));

        List<Book> result = service.findAll("Robert Martin");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).author()).isEqualTo("Robert Martin");
    }

    // ---------- update ----------

    @Test
    void update_existingBook_changesFields() {
        BookEntity existing = entity(1L, "9780132350884");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByIsbn("9780132350884")).thenReturn(Optional.of(existing));
        when(repository.save(any(BookEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book update = new Book(null, "Clean Code, 2nd edition", "Robert Martin", "9780132350884", 2020);
        Book result = service.update(1L, update);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Clean Code, 2nd edition");
        assertThat(result.publishedYear()).isEqualTo(2020);
    }

    @Test
    void update_isbnBelongsToAnotherBook_throwsDuplicate() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity(1L, "9780132350884")));
        when(repository.findByIsbn("9780134685991")).thenReturn(Optional.of(entity(2L, "9780134685991")));

        Book update = new Book(null, "Effective Java", "Joshua Bloch", "9780134685991", 2018);

        assertThatThrownBy(() -> service.update(1L, update))
                .isInstanceOf(DuplicateResourceException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void update_missingBook_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Book update = new Book(null, "Any", "Any", "9780132350884", 2000);

        assertThatThrownBy(() -> service.update(99L, update))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- delete ----------

    @Test
    void delete_existing_deletesById() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_missing_throwsNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}