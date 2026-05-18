package com.example.demo;


import com.example.demo.JavaFXApp;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // needed for due-date notification scheduler
public class LibraryApplication {

    // Spring context made accessible to JavaFX controllers via static ref
    public static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        // Start Spring first, then launch JavaFX
        springContext = SpringApplication.run(LibraryApplication.class, args);
        System.out.println("DLS Spring Boot started.");
        Application.launch(JavaFXApp.class, args);
    }
}