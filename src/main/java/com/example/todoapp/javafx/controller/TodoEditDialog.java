package com.example.todoapp.javafx.controller;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;

public class TodoEditDialog extends Dialog<TodoItem> {

    private TextField titleField;
    private TextArea descriptionArea;
    private ComboBox<Priority> priorityComboBox;
    private DateTimePicker dueDatePicker;
    private CheckBox completedCheckBox;

    public TodoEditDialog(TodoItem todo) {
        setTitle("Chỉnh sửa Todo");
        setHeaderText("Cập nhật thông tin todo");

        // Tạo buttons
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Tạo form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        titleField = new TextField(todo.getTitle());
        titleField.setPrefWidth(300);

        descriptionArea = new TextArea(todo.getDescription());
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPrefWidth(300);

        priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Priority.values());
        priorityComboBox.setValue(todo.getPriority());

        dueDatePicker = new DateTimePicker();
        dueDatePicker.setDateTimeValue(todo.getDueDate());

        completedCheckBox = new CheckBox("Đã hoàn thành");
        completedCheckBox.setSelected(todo.isCompleted());

        grid.add(new Label("Tiêu đề:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Mô tả:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Độ ưu tiên:"), 0, 2);
        grid.add(priorityComboBox, 1, 2);
        grid.add(new Label("Hạn chót:"), 0, 3);
        grid.add(dueDatePicker, 1, 3);
        grid.add(completedCheckBox, 1, 4);

        getDialogPane().setContent(grid);

        // Validation
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            if (titleField.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Tiêu đề không được để trống!");
                alert.showAndWait();
                e.consume();
            }
        });

        // Converter
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                TodoItem updatedTodo = new TodoItem();
                updatedTodo.setId(todo.getId());
                updatedTodo.setTitle(titleField.getText().trim());
                updatedTodo.setDescription(descriptionArea.getText().trim());
                updatedTodo.setPriority(priorityComboBox.getValue());
                updatedTodo.setDueDate(dueDatePicker.getDateTimeValue());
                updatedTodo.setCompleted(completedCheckBox.isSelected());
                return updatedTodo;
            }
            return null;
        });
    }
}