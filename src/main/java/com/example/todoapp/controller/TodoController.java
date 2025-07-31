package com.example.todoapp.controller;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import com.example.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public List<TodoItem> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItem> getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id)
                .map(todo -> ResponseEntity.ok().body(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TodoItem createTodo(@Valid @RequestBody TodoItem todo) {
        return todoService.createTodo(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoItem> updateTodo(@PathVariable Long id,
                                               @Valid @RequestBody TodoItem todoDetails) {
        try {
            TodoItem updatedTodo = todoService.updateTodo(id, todoDetails);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoItem> toggleComplete(@PathVariable Long id) {
        try {
            TodoItem updatedTodo = todoService.toggleComplete(id);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/completed")
    public List<TodoItem> getCompletedTodos() {
        return todoService.getCompletedTodos();
    }

    @GetMapping("/incomplete")
    public List<TodoItem> getIncompleteTodos() {
        return todoService.getIncompleteTodos();
    }

    @GetMapping("/search")
    public List<TodoItem> searchTodos(@RequestParam String keyword) {
        return todoService.searchTodos(keyword);
    }

    @GetMapping("/priority/{priority}")
    public List<TodoItem> getTodosByPriority(@PathVariable Priority priority) {
        return todoService.getTodosByPriority(priority);
    }
}