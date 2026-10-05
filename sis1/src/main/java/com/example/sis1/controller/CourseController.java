package com.example.sis1.controller;

import com.example.sis1.dto.CourseRequest;
import com.example.sis1.dto.CourseResponse;
import com.example.sis1.entity.Course;
import com.example.sis1.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);

        return ResponseEntity.ok(toResponse(course));
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request) {

        Course course = new Course(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getCredits()
        );

        Course savedCourse = courseService.createCourse(course);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedCourse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        Course course = new Course(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getCredits()
        );

        Course updatedCourse = courseService.updateCourse(id, course);

        return ResponseEntity.ok(toResponse(updatedCourse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCredits()
        );
    }
}