package com.example.todoapp.repository;

import com.example.todoapp.model.TodoItem;
import com.example.todoapp.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByCompleted(boolean completed);

    List<TodoItem> findByPriority(Priority priority);

    @Query("SELECT t FROM TodoItem t WHERE t.dueDate BETWEEN ?1 AND ?2")
    List<TodoItem> findByDueDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM TodoItem t WHERE t.title LIKE %?1% OR t.description LIKE %?1%")
    List<TodoItem> findByTitleOrDescriptionContaining(String keyword);

    @Query("SELECT t FROM TodoItem t ORDER BY t.completed ASC, t.priority DESC, t.createdAt DESC")
    List<TodoItem> findAllOrderByCompletedAndPriorityAndCreatedAt();
}