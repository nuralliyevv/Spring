package com.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
		"spring.kafka.listener.auto-startup=false",
		"spring.kafka.admin.auto-create=false"
})
@Import(TestcontainersConfiguration.class)
class LibraryApplicationTests {

	@Test
	void contextLoads() {
	}
}