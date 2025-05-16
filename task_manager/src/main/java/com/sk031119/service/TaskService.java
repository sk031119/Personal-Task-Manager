package com.sk031119.service;

import com.sk031119.model.Task;
import com.sk031119.model.User;
import com.sk031119.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Create or update a task
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
    
    // Save a task for a specific user
    public Task saveTaskForUser(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // Get all tasks for a specific user
    public List<Task> getAllTasksForUser(User user) {
        return taskRepository.findByUser(user);
    }

    // Get task by ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Delete a task
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Get completed tasks for a specific user
    public List<Task> getCompletedTasksForUser(User user) {
        return taskRepository.findByCompletedAndUser(true, user);
    }

    // Get pending tasks for a specific user
    public List<Task> getPendingTasksForUser(User user) {
        return taskRepository.findByCompletedAndUser(false, user);
    }

    // Toggle task completion status
    public Task toggleTaskCompletion(Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setCompleted(!task.isCompleted());
            return taskRepository.save(task);
        }
        throw new IllegalArgumentException("Task with ID " + id + " not found");
    }

    // Search tasks by keyword in title or description for a specific user
    public List<Task> searchTasksForUser(String keyword, User user) {
        return taskRepository.findByTitleContainingIgnoreCaseAndUserOrDescriptionContainingIgnoreCaseAndUser(
            keyword, user, keyword, user);
    }

    // Get all completed tasks (for admin)
    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }

    // Get all pending tasks (for admin)
    public List<Task> getPendingTasks() {
        return taskRepository.findByCompleted(false);
    }
    
    // Search all tasks by keyword (for admin)
    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            keyword, keyword);
    }
}