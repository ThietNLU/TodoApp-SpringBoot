module todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.webmvc;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires java.desktop;
    requires spring.beans;
    requires org.hibernate.orm.core;
    requires spring.boot.starter.thymeleaf;

    // Open packages to Spring and related frameworks
    opens com.example.todoapp to spring.core, spring.beans, spring.context;
    opens com.example.todoapp.model to org.hibernate.orm.core, spring.core;
    opens com.example.todoapp.javafx.controller to javafx.fxml, spring.core;
    opens com.example.todoapp.controller to spring.core, spring.beans, spring.web;
    opens com.example.todoapp.service to spring.core, spring.beans;
    opens com.example.todoapp.repository to spring.core, spring.data.jpa;
    opens com.example.todoapp.javafx.service to spring.core;

    // Export packages
    exports com.example.todoapp;
    exports com.example.todoapp.javafx;
    exports com.example.todoapp.model;
    exports com.example.todoapp.controller;
    exports com.example.todoapp.service;
    exports com.example.todoapp.javafx.controller;
    exports com.example.todoapp.javafx.service;
}
