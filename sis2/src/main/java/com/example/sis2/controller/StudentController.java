
package com.example.sis2.controller;

import com.example.sis2.domain.Student;
import com.example.sis2.dto.StudentRequestDto;
import com.example.sis2.dto.StudentResponseDto;
import com.example.sis2.mapper.StudentMapper;
import com.example.sis2.service.StudentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;
    private final StudentMapper mapper;

    public StudentController(StudentService service,
                             StudentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(
            @Valid @RequestBody StudentRequestDto request) {

        Student student = mapper.toDomain(request);
        Student saved = service.createStudent(student);

        return ResponseEntity
                .created(URI.create("/api/students/" + saved.getId()))
                .body(mapper.toResponseDto(saved));
    }

    // READ ALL
    @GetMapping
    public List<StudentResponseDto> getAllStudents() {

        return service.getAllStudents()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    // READ ONE
    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(
            @PathVariable Long id) {

        return mapper.toResponseDto(
                service.getStudentById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public StudentResponseDto updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto request) {

        Student updatedStudent = mapper.toDomain(request);

        return mapper.toResponseDto(
                service.updateStudent(id, updatedStudent)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
    }

    // HANDLE STUDENT NOT FOUND
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(
            NoSuchElementException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // HANDLE DUPLICATE EMAIL
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(
            IllegalArgumentException ex) {

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
