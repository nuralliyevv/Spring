package com.example.sis1.service;

import com.example.sis1.entity.Course;
import com.example.sis1.repository.CourseRepository;
import org.springframework.stereotype.Service;

import com.example.sis1.exception.CourseNotFoundException;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course course) {
        Course existingCourse = getCourseById(id);

        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setCredits(course.getCredits());

        return existingCourse;
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.deleteById(course.getId());
    }
}