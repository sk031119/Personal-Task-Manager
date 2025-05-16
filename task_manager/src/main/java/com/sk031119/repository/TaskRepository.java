package com.sk031119.repository;

import com.sk031119.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);
    
    // Find tasks by priority
    List<Task> findByPriority(Task.Priority priority);
    
    // Find tasks containing title or description (case insensitive search)
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
} 