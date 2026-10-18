
package com.example.sis2.repository;

import com.example.sis2.entity.StudentEntity;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class StudentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private StudentRepository repository;

    @Autowired
    private DataSource dataSource;

    @Test
    void saveAndFindStudentByEmail() throws Exception {

        // Confirm we are using real PostgreSQL
        try (Connection connection = dataSource.getConnection()) {
            assertEquals(
                    "PostgreSQL",
                    connection.getMetaData().getDatabaseProductName()
            );
        }

        // Create an entity
        StudentEntity student = new StudentEntity(
                null,
                "Alice",
                "Johnson",
                "alice@example.com"
        );

        // Save and synchronize with the database
        repository.saveAndFlush(student);

        // Retrieve by email
        Optional<StudentEntity> result =
                repository.findByEmail("alice@example.com");

        // Verify
        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        assertEquals("Alice", result.get().getFirstName());
        assertEquals("Johnson", result.get().getLastName());
        assertEquals("alice@example.com", result.get().getEmail());
    }
}
