package com.sk031119.repository;

import com.sk031119.model.Task;
import com.sk031119.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by user
    List<Task> findByUser(User user);
    
    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);
    
    // Find tasks by completion status and user
    List<Task> findByCompletedAndUser(boolean completed, User user);
    
    // Find tasks by priority and user
    List<Task> findByPriorityAndUser(Task.Priority priority, User user);
    
    // Find tasks containing title or description (case insensitive search)
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description);
    
    // Find tasks containing title or description for a specific user (case insensitive search)
    List<Task> findByTitleContainingIgnoreCaseAndUserOrDescriptionContainingIgnoreCaseAndUser(
        String title, User user1, String description, User user2);
} 