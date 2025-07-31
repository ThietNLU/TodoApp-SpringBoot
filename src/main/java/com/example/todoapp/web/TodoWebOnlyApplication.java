package com.example.todoapp.web;

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
public class TodoWebOnlyApplication {
    public static void main(String[] args) {
        // Disable JavaFX and module path issues
        System.setProperty("java.awt.headless", "true");
        System.setProperty("javafx.embed.isEventThread", "false");

        SpringApplication app = new SpringApplication(TodoWebOnlyApplication.class);
        // Disable modules for this web-only version
        app.run(args);
    }
}
