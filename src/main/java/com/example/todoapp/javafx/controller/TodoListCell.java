package com.example.todoapp.javafx.controller;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class TodoListCell extends ListCell<TodoItem> {

    private HBox content;
    private CheckBox checkBox;
    private VBox textContainer;
    private Label titleLabel;
    private Label descriptionLabel;
    private Label priorityLabel;
    private Label dueDateLabel;

    public TodoListCell() {
        createContent();
    }

    private void createContent() {
        checkBox = new CheckBox();
        checkBox.setOnAction(e -> {
            TodoItem item = getItem();
            if (item != null) {
                // Trigger toggle complete action
                // This should be handled by the parent controller
            }
        });

        titleLabel = new Label();
        titleLabel.getStyleClass().add("todo-title");

        descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("todo-description");

        priorityLabel = new Label();
        priorityLabel.getStyleClass().add("todo-priority");

        dueDateLabel = new Label();
        dueDateLabel.getStyleClass().add("todo-due-date");

        textContainer = new VBox(2);
        textContainer.getChildren().addAll(titleLabel, descriptionLabel);

        HBox metaContainer = new HBox(10);
        metaContainer.getChildren().addAll(priorityLabel, dueDateLabel);

        VBox fullTextContainer = new VBox(5);
        fullTextContainer.getChildren().addAll(textContainer, metaContainer);

        content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(8));
        content.getChildren().addAll(checkBox, fullTextContainer);

        setGraphic(content);
    }

    @Override
    protected void updateItem(TodoItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            updateContent(item);
            setGraphic(content);
        }
    }

    private void updateContent(TodoItem item) {
        checkBox.setSelected(item.isCompleted());

        titleLabel.setText(item.getTitle());
        if (item.isCompleted()) {
            titleLabel.getStyleClass().removeAll("todo-title");
            titleLabel.getStyleClass().addAll("todo-title", "completed");
        } else {
            titleLabel.getStyleClass().removeAll("todo-title", "completed");
            titleLabel.getStyleClass().add("todo-title");
        }

        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            descriptionLabel.setText(item.getDescription());
            descriptionLabel.setVisible(true);
            descriptionLabel.setManaged(true);
        } else {
            descriptionLabel.setVisible(false);
            descriptionLabel.setManaged(false);
        }

        updatePriorityLabel(item.getPriority());

        if (item.getDueDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dueDateLabel.setText("Hạn: " + item.getDueDate().format(formatter));
            dueDateLabel.setVisible(true);
            dueDateLabel.setManaged(true);
        } else {
            dueDateLabel.setVisible(false);
            dueDateLabel.setManaged(false);
        }
    }

    private void updatePriorityLabel(Priority priority) {
        priorityLabel.getStyleClass().clear();
        priorityLabel.getStyleClass().add("todo-priority");

        switch (priority) {
            case HIGH:
                priorityLabel.setText("Cao");
                priorityLabel.getStyleClass().add("priority-high");
                break;
            case LOW:
                priorityLabel.setText("Thấp");
                priorityLabel.getStyleClass().add("priority-low");
                break;
            default:
                priorityLabel.setText("Bình thường");
                priorityLabel.getStyleClass().add("priority-normal");
                break;
        }
    }
}