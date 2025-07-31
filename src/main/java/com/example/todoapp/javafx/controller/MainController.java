package com.example.todoapp.javafx.controller;

import com.example.todoapp.javafx.service.TodoApiService;
import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.event.ActionEvent;

@Component
public class MainController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TextField newTodoField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Priority> priorityComboBox;
    @FXML private HBox dueDateContainer;
    @FXML private DateTimePicker dueDatePicker;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private ListView<TodoItem> todoListView;
    @FXML private Label statusLabel;
    @FXML private CheckMenuItem showCompletedCheckBox;
    @FXML private VBox detailsPane;
    @FXML private Label selectedTitleLabel;
    @FXML private Label selectedDescriptionLabel;
    @FXML private Label selectedPriorityLabel;
    @FXML private Label selectedDueDateLabel;
    @FXML private Label selectedCreatedLabel;

    @Autowired
    private TodoApiService todoApiService;

    private ObservableList<TodoItem> todoItems = FXCollections.observableArrayList();
    private ObservableList<TodoItem> filteredItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListView();
        setupPriorityComboBox();
        setupDatePicker();
        setupEventHandlers();
        loadTodos();
    }

    private void setupListView() {
        todoListView.setItems(filteredItems);
        todoListView.setCellFactory(listView -> new TodoListCell());

        todoListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                updateButtonStates(newValue != null);
                if (newValue != null) {
                    showTodoDetails(newValue);
                } else {
                    hideTodoDetails();
                }
            });
    }

    private void setupPriorityComboBox() {
        priorityComboBox.setItems(FXCollections.observableArrayList(Priority.values()));
        priorityComboBox.setValue(Priority.NORMAL);
    }

    private void setupDatePicker() {
        dueDatePicker = new DateTimePicker();
        // Đảm bảo dueDateContainer được inject từ FXML
        Platform.runLater(() -> {
            if (dueDateContainer != null) {
                dueDateContainer.getChildren().add(dueDatePicker);
            }
        });
    }

    private void setupEventHandlers() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTodos());

        showCompletedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> filterTodos());

        newTodoField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTodo();
            }
        });
    }

    private void updateButtonStates(boolean todoSelected) {
        editButton.setDisable(!todoSelected);
        deleteButton.setDisable(!todoSelected);
    }

    private void showTodoDetails(TodoItem todo) {
        detailsPane.setVisible(true);
        selectedTitleLabel.setText(todo.getTitle());
        selectedDescriptionLabel.setText(todo.getDescription() != null ? todo.getDescription() : "Không có mô tả");
        selectedPriorityLabel.setText(getPriorityText(todo.getPriority()));
        selectedDueDateLabel.setText(todo.getDueDate() != null ?
            todo.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Không có hạn chót");
        selectedCreatedLabel.setText(todo.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    private void hideTodoDetails() {
        detailsPane.setVisible(false);
    }

    private String getPriorityText(Priority priority) {
        switch (priority) {
            case LOW: return "Thấp";
            case NORMAL: return "Bình thường";
            case HIGH: return "Cao";
            default: return "Không xác định";
        }
    }

    @FXML
    public void addTodo() {
        String title = newTodoField.getText().trim();
        String description = descriptionArea.getText().trim();
        Priority priority = priorityComboBox.getValue();
        LocalDateTime dueDate = dueDatePicker != null ? dueDatePicker.getDateTimeValue() : null;

        if (title.isEmpty()) {
            showAlert("Lỗi", "Tiêu đề không được để trống");
            return;
        }

        TodoItem newTodo = new TodoItem();
        newTodo.setTitle(title);
        newTodo.setDescription(description.isEmpty() ? null : description);
        newTodo.setPriority(priority);
        newTodo.setDueDate(dueDate);
        newTodo.setCompleted(false);
        newTodo.setCreatedAt(LocalDateTime.now());

        saveTodo(newTodo);
        clearForm();
    }

    @FXML
    public void editTodo() {
        TodoItem selectedTodo = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTodo != null) {
            TodoEditDialog dialog = new TodoEditDialog(selectedTodo);
            dialog.showAndWait().ifPresent(updatedTodo -> {
                updateTodo(updatedTodo);
            });
        }
    }

    @FXML
    public void deleteTodo() {
        TodoItem selectedTodo = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTodo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa todo này?");
            alert.setContentText(selectedTodo.getTitle());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteTodoFromServer(selectedTodo);
                }
            });
        }
    }

    @FXML
    public void toggleComplete() {
        TodoItem selectedTodo = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTodo != null) {
            selectedTodo.setCompleted(!selectedTodo.isCompleted());
            updateTodo(selectedTodo);
        }
    }

    private void clearForm() {
        newTodoField.clear();
        descriptionArea.clear();
        priorityComboBox.setValue(Priority.NORMAL);
        if (dueDatePicker != null) {
            dueDatePicker.setDateTimeValue(null);
        }
    }

    private void filterTodos() {
        String searchText = searchField.getText().toLowerCase();
        boolean showCompleted = showCompletedCheckBox.isSelected();

        filteredItems.clear();
        for (TodoItem todo : todoItems) {
            boolean matchesSearch = searchText.isEmpty() ||
                todo.getTitle().toLowerCase().contains(searchText) ||
                (todo.getDescription() != null && todo.getDescription().toLowerCase().contains(searchText));

            boolean shouldShow = showCompleted || !todo.isCompleted();

            if (matchesSearch && shouldShow) {
                filteredItems.add(todo);
            }
        }
    }

    private void loadTodos() {
        statusLabel.setText("Đang tải todos...");

        CompletableFuture.supplyAsync(() -> todoApiService.getAllTodos())
            .thenAccept(todos -> Platform.runLater(() -> {
                todoItems.setAll(todos);
                filterTodos();
                statusLabel.setText("Đã tải " + todos.size() + " todos");
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Không thể tải danh sách todos: " + throwable.getMessage());
                    statusLabel.setText("Lỗi tải dữ liệu");
                });
                return null;
            });
    }

    private void saveTodo(TodoItem todo) {
        statusLabel.setText("Đang lưu todo...");

        CompletableFuture.supplyAsync(() -> todoApiService.createTodo(todo))
            .thenAccept(savedTodo -> Platform.runLater(() -> {
                todoItems.add(savedTodo);
                filterTodos();
                statusLabel.setText("Đã lưu todo thành công");
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Không thể lưu todo: " + throwable.getMessage());
                    statusLabel.setText("Lỗi lưu dữ liệu");
                });
                return null;
            });
    }

    private void updateTodo(TodoItem todo) {
        statusLabel.setText("Đang cập nhật todo...");

        CompletableFuture.supplyAsync(() -> todoApiService.updateTodo(todo.getId(), todo))
            .thenAccept(updatedTodo -> Platform.runLater(() -> {
                int index = todoItems.indexOf(todo);
                if (index >= 0) {
                    todoItems.set(index, updatedTodo);
                    filterTodos();
                    showTodoDetails(updatedTodo);
                }
                statusLabel.setText("Đã cập nhật todo thành công");
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Không thể cập nhật todo: " + throwable.getMessage());
                    statusLabel.setText("Lỗi cập nhật dữ liệu");
                });
                return null;
            });
    }

    private void deleteTodoFromServer(TodoItem todo) {
        statusLabel.setText("Đang xóa todo...");

        CompletableFuture.runAsync(() -> todoApiService.deleteTodo(todo.getId()))
            .thenRun(() -> Platform.runLater(() -> {
                todoItems.remove(todo);
                filterTodos();
                hideTodoDetails();
                statusLabel.setText("Đã xóa todo thành công");
            }))
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    showAlert("Lỗi", "Không thể xóa todo: " + throwable.getMessage());
                    statusLabel.setText("Lỗi xóa dữ liệu");
                });
                return null;
            });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }

    @FXML
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Về ứng dụng");
        alert.setHeaderText("Todo Application");
        alert.setContentText("Ứng dụng quản lý công việc đơn giản\nPhiên bản 1.0");
        alert.showAndWait();
    }
}
