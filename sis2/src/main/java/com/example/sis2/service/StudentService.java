
package com.example.sis2.service;

import com.example.sis2.domain.Student;
import com.example.sis2.entity.StudentEntity;
import com.example.sis2.mapper.StudentMapper;
import com.example.sis2.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository repository,
                          StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    @Transactional
    public Student createStudent(Student student) {

        if (repository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        StudentEntity entity = mapper.toEntity(student);
        StudentEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    // READ ONE
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {

        StudentEntity entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Student not found with id: " + id
                ));

        return mapper.toDomain(entity);
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {

        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    // UPDATE
    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {

        StudentEntity existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Student not found with id: " + id
                ));

        repository.findByEmail(updatedStudent.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new IllegalArgumentException(
                            "Email already exists"
                    );
                });

        existing.setFirstName(updatedStudent.getFirstName());
        existing.setLastName(updatedStudent.getLastName());
        existing.setEmail(updatedStudent.getEmail());

        StudentEntity savedEntity = repository.save(existing);

        return mapper.toDomain(savedEntity);
    }

    // DELETE
    @Transactional
    public void deleteStudent(Long id) {

        if (!repository.existsById(id)) {
            throw new NoSuchElementException(
                    "Student not found with id: " + id
            );
        }

        repository.deleteById(id);
    }
}
