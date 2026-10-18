
package com.example.sis2.service;

import com.example.sis2.domain.Student;
import com.example.sis2.entity.StudentEntity;
import com.example.sis2.mapper.StudentMapper;
import com.example.sis2.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @Spy
    private StudentMapper mapper = new StudentMapper();

    @InjectMocks
    private StudentService service;

    private Student student;
    private StudentEntity entity;

    @BeforeEach
    void setUp() {
        student = new Student(
                null, "John", "Smith", "john@example.com"
        );

        entity = new StudentEntity(
                1L, "John", "Smith", "john@example.com"
        );
    }

    @Test
    void createStudent_success() {
        when(repository.existsByEmail("john@example.com"))
                .thenReturn(false);

        when(repository.save(any(StudentEntity.class)))
                .thenReturn(entity);

        Student result = service.createStudent(student);

        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("john@example.com", result.getEmail());

        verify(repository).save(any(StudentEntity.class));
    }

    @Test
    void createStudent_duplicateEmail() {
        when(repository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createStudent(student)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void getStudentById_success() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(entity));

        Student result = service.getStudentById(1L);

        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getStudentById_notFound() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> service.getStudentById(99L)
        );
    }

    @Test
    void getAllStudents_success() {
        when(repository.findAll())
                .thenReturn(List.of(entity));

        List<Student> result = service.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void updateStudent_success() {
        Student updated = new Student(
                null, "John", "Johnson",
                "newjohn@example.com"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(entity));

        when(repository.findByEmail("newjohn@example.com"))
                .thenReturn(Optional.empty());

        when(repository.save(any(StudentEntity.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Student result = service.updateStudent(1L, updated);

        assertEquals("Johnson", result.getLastName());
        assertEquals("newjohn@example.com", result.getEmail());

        verify(repository).save(any(StudentEntity.class));
    }

    @Test
    void deleteStudent_success() {
        when(repository.existsById(1L))
                .thenReturn(true);

        service.deleteStudent(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteStudent_notFound() {
        when(repository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                NoSuchElementException.class,
                () -> service.deleteStudent(99L)
        );

        verify(repository, never()).deleteById(anyLong());
    }
}
