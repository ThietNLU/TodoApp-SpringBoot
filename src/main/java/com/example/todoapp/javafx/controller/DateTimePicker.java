package com.example.todoapp.javafx.controller;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimePicker extends HBox {

    private DatePicker datePicker;
    private TextField timeField;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public DateTimePicker() {
        super(5);

        datePicker = new DatePicker();
        timeField = new TextField();
        timeField.setPromptText("HH:MM");
        timeField.setPrefWidth(80);

        // Validation cho time field
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d{0,2}:?\\d{0,2}")) {
                timeField.setText(oldValue);
            }
        });

        getChildren().addAll(datePicker, timeField);
    }

    public LocalDateTime getDateTimeValue() {
        LocalDate date = datePicker.getValue();
        if (date == null) return null;

        LocalTime time = LocalTime.of(0, 0); // Default time

        String timeText = timeField.getText().trim();
        if (!timeText.isEmpty()) {
            try {
                time = LocalTime.parse(timeText, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                // Use default time if parsing fails
            }
        }

        return LocalDateTime.of(date, time);
    }

    public void setDateTimeValue(LocalDateTime dateTime) {
        if (dateTime == null) {
            datePicker.setValue(null);
            timeField.setText("");
        } else {
            datePicker.setValue(dateTime.toLocalDate());
            timeField.setText(dateTime.toLocalTime().format(TIME_FORMATTER));
        }
    }
}