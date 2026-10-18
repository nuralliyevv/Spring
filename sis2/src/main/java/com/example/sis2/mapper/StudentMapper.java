
package com.example.sis2.mapper;

import com.example.sis2.domain.Student;
import com.example.sis2.dto.StudentRequestDto;
import com.example.sis2.dto.StudentResponseDto;
import com.example.sis2.entity.StudentEntity;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    // Request DTO -> Domain
    public Student toDomain(StudentRequestDto dto) {
        return new Student(
                null,
                dto.firstName(),
                dto.lastName(),
                dto.email()
        );
    }

    // Domain -> Entity
    public StudentEntity toEntity(Student student) {
        return new StudentEntity(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

    // Entity -> Domain
    public Student toDomain(StudentEntity entity) {
        return new Student(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail()
        );
    }

    // Domain -> Response DTO
    public StudentResponseDto toResponseDto(Student student) {
        return new StudentResponseDto(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }
}
