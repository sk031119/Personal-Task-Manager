package com.sk031119.config;

import com.sk031119.model.Task;
import com.sk031119.model.User;
import com.sk031119.repository.TaskRepository;
import com.sk031119.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Only load sample data if the repositories are empty
        if (userRepository.count() == 0) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        System.out.println("Loading sample users and tasks...");

        // Create admin account
        User admin = new User("admin", "admin", "System Administrator", User.Role.ADMIN);
        
        // Create regular demo users
        User user1 = new User("user1", "password1", "John Doe");
        User user2 = new User("user2", "password2", "Jane Smith");
        
        // Save users
        userRepository.save(admin);
        userRepository.save(user1);
        userRepository.save(user2);
        
        System.out.println("Admin account created: username='admin', password='admin123'");
        
        // Create some sample tasks for user1
        Task task1 = new Task(
                "Complete project prototype",
                "Finish the initial prototype for the client meeting",
                LocalDate.now().plusDays(7),
                Task.Priority.HIGH
        );
        task1.setUser(user1);

        Task task2 = new Task(
                "Buy groceries",
                "Milk, eggs, bread, fruits, vegetables",
                LocalDate.now().plusDays(2),
                Task.Priority.MEDIUM
        );
        task2.setUser(user1);

        // Create some sample tasks for user2
        Task task3 = new Task(
                "Schedule dentist appointment",
                "Call Dr. Smith's office to schedule a checkup",
                LocalDate.now().plusDays(14),
                Task.Priority.LOW
        );
        task3.setUser(user2);
        
        Task task4 = new Task(
                "Prepare presentation",
                "Create slides for next week's meeting",
                LocalDate.now().plusDays(5),
                Task.Priority.HIGH
        );
        task4.setUser(user2);

        // Save all tasks
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        
        System.out.println("Sample data loaded successfully");
    }
}