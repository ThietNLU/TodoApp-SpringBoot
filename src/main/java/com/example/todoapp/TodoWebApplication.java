package com.example.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.todoapp.controller",
    "com.example.todoapp.service",
    "com.example.todoapp.repository",
    "com.example.todoapp.model"
})
public class TodoWebApplication {
    public static void main(String[] args) {
        // Disable JavaFX-related system properties
        System.setProperty("java.awt.headless", "true");
        System.setProperty("javafx.embed.isEventThread", "false");

        SpringApplication app = new SpringApplication(TodoWebApplication.class);
        app.run(args);
    }
}
