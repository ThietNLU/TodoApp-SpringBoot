module todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires java.desktop;
    requires spring.beans;
    requires org.hibernate.orm.core;

    opens com.example.todoapp to spring.core;
    opens com.example.todoapp.model;
    opens com.example.todoapp.javafx.controller;
    opens com.example.todoapp.controller;
    opens com.example.todoapp.service;
    opens com.example.todoapp.repository to spring.core;
    opens com.example.todoapp.javafx.service to spring.core;

    exports com.example.todoapp;
    exports com.example.todoapp.javafx;
    exports com.example.todoapp.model;
    exports com.example.todoapp.controller to spring.beans;
    exports com.example.todoapp.service to spring.beans;
    exports com.example.todoapp.javafx.controller to spring.beans;
    exports com.example.todoapp.javafx.service to spring.beans;
}
