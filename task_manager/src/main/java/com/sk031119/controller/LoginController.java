package com.sk031119.controller;

import com.sk031119.TaskManagerApplication;
import com.sk031119.model.User;
import com.sk031119.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;
    
    private final UserService userService;
    
    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Clear message label initially
        messageLabel.setText("");
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password are required");
            return;
        }
        
        if (userService.authenticate(username, password)) {
            try {
                // Load the user object
                User user = userService.getUserByUsername(username).orElseThrow();
                
                // Store the logged-in user
                TaskManagerApplication.setCurrentUser(user);
                
                // Show temporary login success message
                String roleText = user.isAdmin() ? "Administrator" : "User";
                messageLabel.setText("Login successful! Welcome " + roleText + " " + user.getName());
                messageLabel.setStyle("-fx-text-fill: green;");
                
                // Navigate to task dashboard
                TaskManagerApplication.setRoot("task-dashboard");
            } catch (IOException e) {
                messageLabel.setText("Error loading task dashboard");
                messageLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Invalid username or password");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String name = nameField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            messageLabel.setText("All fields are required for registration");
            return;
        }
        
        // Check if username already exists
        if (userService.usernameExists(username)) {
            messageLabel.setText("Username already exists");
            return;
        }
        
        // Create and save the new user
        User newUser = new User(username, password, name);
        userService.createUser(newUser);
        
        // Show success message
        messageLabel.setText("Registration successful! You can now login.");
        
        // Clear the form
        nameField.clear();
        passwordField.clear();
    }
}
