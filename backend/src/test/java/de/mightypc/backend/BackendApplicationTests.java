package de.mightypc.backend;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
        "de.flapdoodle.mongodb.embedded.version=7.0.3"
})
class BackendApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "The application context should have loaded.");
    }

    @Test
    void cloudinaryBeanShouldBeConfigured() {
        // Arrange & Act
        Cloudinary cloudinary = applicationContext.getBean(Cloudinary.class);

        // Assert
        assertNotNull(cloudinary, "The Cloudinary bean should be configured.");
    }

    @Test
    void mainMethodTest() {
        // Arrange
        String[] args = {};

        // Act
        BackendApplication.main(args);

        // Assert
        assertNotNull(applicationContext, "The application context should have loaded after running the main method.");
    }
}
