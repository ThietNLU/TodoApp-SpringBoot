package com.example.todoapp.service;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import com.example.todoapp.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoItemRepository todoRepository;

    public List<TodoItem> getAllTodos() {
        return todoRepository.findAllOrderByCompletedAndPriorityAndCreatedAt();
    }

    public Optional<TodoItem> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    public TodoItem createTodo(TodoItem todo) {
        return todoRepository.save(todo);
    }

    public TodoItem updateTodo(Long id, TodoItem todoDetails) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setCompleted(todoDetails.isCompleted());
        todo.setDueDate(todoDetails.getDueDate());
        todo.setPriority(todoDetails.getPriority());

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    public List<TodoItem> getCompletedTodos() {
        return todoRepository.findByCompleted(true);
    }

    public List<TodoItem> getIncompleteTodos() {
        return todoRepository.findByCompleted(false);
    }

    public List<TodoItem> getTodosByPriority(Priority priority) {
        return todoRepository.findByPriority(priority);
    }

    public List<TodoItem> searchTodos(String keyword) {
        return todoRepository.findByTitleOrDescriptionContaining(keyword);
    }

    public TodoItem toggleComplete(Long id) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        todo.setCompleted(!todo.isCompleted());
        return todoRepository.save(todo);
    }
}