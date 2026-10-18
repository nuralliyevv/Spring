
package com.example.sis2.controller;

import com.example.sis2.domain.Student;
import com.example.sis2.mapper.StudentMapper;
import com.example.sis2.service.StudentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(StudentMapper.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService service;

    // Test POST /api/students
    @Test
    void createStudent_success() throws Exception {

        Student saved = new Student(
                1L, "John", "Smith", "john@example.com"
        );

        when(service.createStudent(any(Student.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "John",
                              "lastName": "Smith",
                              "email": "john@example.com"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location", "/api/students/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));

        verify(service).createStudent(any(Student.class));
    }

    // Test GET /api/students/1
    @Test
    void getStudentById_success() throws Exception {

        Student student = new Student(
                1L, "John", "Smith", "john@example.com"
        );

        when(service.getStudentById(1L))
                .thenReturn(student);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    // Test GET /api/students
    @Test
    void getAllStudents_success() throws Exception {

        Student student = new Student(
                1L, "John", "Smith", "john@example.com"
        );

        when(service.getAllStudents())
                .thenReturn(List.of(student));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    // Test PUT /api/students/1
    @Test
    void updateStudent_success() throws Exception {

        Student updated = new Student(
                1L, "John", "Johnson", "newjohn@example.com"
        );

        when(service.updateStudent(eq(1L), any(Student.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "John",
                              "lastName": "Johnson",
                              "email": "newjohn@example.com"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email")
                        .value("newjohn@example.com"));
    }

    // Test DELETE /api/students/1
    @Test
    void deleteStudent_success() throws Exception {

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteStudent(1L);
    }

    // Test 404 Not Found
    @Test
    void getStudentById_notFound() throws Exception {

        when(service.getStudentById(99L))
                .thenThrow(new NoSuchElementException(
                        "Student not found with id: 99"
                ));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        "Student not found with id: 99"
                ));
    }

    // Test invalid email
    @Test
    void createStudent_invalidEmail() throws Exception {

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "John",
                              "lastName": "Smith",
                              "email": "invalid-email"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // Test duplicate email
    @Test
    void createStudent_duplicateEmail() throws Exception {

        when(service.createStudent(any(Student.class)))
                .thenThrow(new IllegalArgumentException(
                        "Email already exists"
                ));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "firstName": "John",
                              "lastName": "Smith",
                              "email": "john@example.com"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Email already exists"
                ));
    }
}
