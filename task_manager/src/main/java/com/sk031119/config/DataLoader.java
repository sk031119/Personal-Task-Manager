package com.sk031119.config;

import com.sk031119.model.Task;
import com.sk031119.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;

    @Autowired
    public DataLoader(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        // Only load sample data if the repository is empty
        if (taskRepository.count() == 0) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        // Create some sample tasks
        Task task1 = new Task(
                "Complete project prototype",
                "Finish the initial prototype for the client meeting",
                LocalDate.now().plusDays(7),
                Task.Priority.HIGH
        );

        Task task2 = new Task(
                "Buy groceries",
                "Milk, eggs, bread, fruits, vegetables",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM
        );

        Task task3 = new Task(
                "Schedule dentist appointment",
                "Call Dr. Smith's office to schedule a checkup",
                LocalDate.now().plusDays(14),
                Task.Priority.LOW
        );

        Task task4 = new Task(
                "Pay electricity bill",
                "Pay the monthly electricity bill online",
                LocalDate.now().plusDays(5),
                Task.Priority.HIGH
        );
        task4.setCompleted(true);

        Task task5 = new Task(
                "Clean the garage",
                "Sort through items and organize the garage",
                LocalDate.now().plusDays(10),
                Task.Priority.MEDIUM
        );

        // Save the tasks
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        taskRepository.save(task5);
    }
} 