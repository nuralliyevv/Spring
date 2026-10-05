package com.example.sis1.repository;

import com.example.sis1.entity.Course;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

    private final List<Course> courses = new ArrayList<>();
    private long nextId = 1;

    public List<Course> findAll() {
        return courses;
    }

    public Optional<Course> findById(Long id) {
        return courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();
    }

    public Course save(Course course) {
        course.setId(nextId++);
        courses.add(course);
        return course;
    }

    public void deleteById(Long id) {
        courses.removeIf(course -> course.getId().equals(id));
    }

    public boolean existsById(Long id) {
        return courses.stream()
                .anyMatch(course -> course.getId().equals(id));
    }
}