package com.sk031119.controller;

import com.sk031119.TaskManagerApplication;
import com.sk031119.model.User;
import com.sk031119.service.TaskService;
import com.sk031119.service.UserService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class AdminPanelController implements Initializable {

    @FXML private Button backToTasksButton;
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Long> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Integer> taskCountColumn;
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;
    @FXML private TextField userSearchField;
    
    // Statistics elements
    @FXML private Label totalUsersLabel;
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label pendingTasksLabel;
    
    private final UserService userService;
    private final TaskService taskService;
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private User selectedUser;
    
    @Autowired
    public AdminPanelController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if user is admin
        User currentUser = TaskManagerApplication.getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", 
                      "You do not have permission to access the admin panel");
            try {
                TaskManagerApplication.setRoot("task-dashboard");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        setupUserTableView();
        loadAllUsers();
        updateStatistics();
        
        // Initially disable edit and delete buttons
        editUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        
        // Listen for table selection changes
        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
            boolean userSelected = (newSelection != null);
            editUserButton.setDisable(!userSelected);
            deleteUserButton.setDisable(!userSelected);
        });
        
        // Setup search functionality
        userSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadAllUsers();
            } else {
                searchUsers(newValue);
            }
        });
    }
    
    private void setupUserTableView() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        roleColumn.setCellValueFactory(data -> {
            User user = data.getValue();
            String roleText = user.isAdmin() ? "Administrator" : "Regular User";
            return new SimpleStringProperty(roleText);
        });
        
        taskCountColumn.setCellValueFactory(data -> {
            User user = data.getValue();
            int taskCount = user.getTasks().size();
            return new SimpleIntegerProperty(taskCount).asObject();
        });
        
        userTableView.setItems(userList);
    }
    
    private void loadAllUsers() {
        userList.clear();
        userList.addAll(userService.getAllUsers());
    }
    
    private void searchUsers(String keyword) {
        // This is a simple implementation. For a real app, you might want a server-side search.
        userList.clear();
        List<User> allUsers = userService.getAllUsers();
        
        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(keyword.toLowerCase()) || 
                user.getName().toLowerCase().contains(keyword.toLowerCase())) {
                userList.add(user);
            }
        }
    }
    
    private void updateStatistics() {
        int totalUsers = userService.getAllUsers().size();
        int totalTasks = taskService.getAllTasks().size();
        int completedTasks = taskService.getCompletedTasks().size();
        int pendingTasks = taskService.getPendingTasks().size();
        
        totalUsersLabel.setText(String.valueOf(totalUsers));
        totalTasksLabel.setText(String.valueOf(totalTasks));
        completedTasksLabel.setText(String.valueOf(completedTasks));
        pendingTasksLabel.setText(String.valueOf(pendingTasks));
    }
    
    @FXML
    private void handleBackToTasks() {
        try {
            TaskManagerApplication.setRoot("task-dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                      "Could not return to task dashboard.");
        }
    }
    
    @FXML
    private void handleAddUser() {
        // Create a dialog for adding a new user
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the username, password, name and role fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField nameField = new TextField();
        ComboBox<User.Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(User.Role.values());
        roleComboBox.setValue(User.Role.USER);
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default
        usernameField.requestFocus();
        
        // Convert the result to a User object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new User(
                    usernameField.getText().trim(),
                    passwordField.getText(),
                    nameField.getText().trim(),
                    roleComboBox.getValue()
                );
            }
            return null;
        });
        
        Optional<User> result = dialog.showAndWait();
        
        result.ifPresent(user -> {
            try {
                // Check if username exists
                if (userService.usernameExists(user.getUsername())) {
                    showAlert(Alert.AlertType.ERROR, "Username Exists", 
                              "The username already exists. Please choose a different username.");
                    return;
                }
                
                // Create the new user
                User newUser = userService.createUser(user);
                loadAllUsers();
                updateStatistics();
                
                // Select the new user in the table
                userTableView.getSelectionModel().select(newUser);
                
                showAlert(Alert.AlertType.INFORMATION, "User Created", 
                          "New user '" + user.getUsername() + "' has been created successfully.");
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "User Creation Failed", 
                          "Could not create user: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    @FXML
    private void handleEditUser() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a user to edit");
            return;
        }
        
        // Create a dialog for editing the user
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details for: " + selectedUser.getUsername());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField(selectedUser.getUsername());
        usernameField.setDisable(true); // Don't allow changing username
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new password (leave blank to keep current)");
        
        TextField nameField = new TextField(selectedUser.getName());
        
        ComboBox<User.Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(User.Role.values());
        roleComboBox.setValue(selectedUser.getRole());
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        nameField.requestFocus();
        
        // Convert the result when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Create a copy of the selected user
                User updatedUser = selectedUser;
                
                // Update the fields
                if (!passwordField.getText().isEmpty()) {
                    updatedUser.setPassword(passwordField.getText());
                }
                
                updatedUser.setName(nameField.getText().trim());
                updatedUser.setRole(roleComboBox.getValue());
                
                return updatedUser;
            }
            return null;
        });
        
        Optional<User> result = dialog.showAndWait();
        
        result.ifPresent(user -> {
            try {
                // Save the updated user
                User updatedUser = userService.updateUser(user);
                loadAllUsers();
                
                // Select the updated user in the table
                userTableView.getSelectionModel().select(updatedUser);
                
                showAlert(Alert.AlertType.INFORMATION, "User Updated", 
                          "User '" + user.getUsername() + "' has been updated successfully.");
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "User Update Failed", 
                          "Could not update user: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    @FXML
    private void handleDeleteUser() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a user to delete");
            return;
        }
        
        // Don't allow deleting the current admin user (self-deletion)
        User currentUser = TaskManagerApplication.getCurrentUser();
        if (selectedUser.getId().equals(currentUser.getId())) {
            showAlert(Alert.AlertType.ERROR, "Cannot Delete", 
                      "You cannot delete your own account.");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User: " + selectedUser.getUsername());
        confirmation.setContentText(
                "Are you sure you want to delete this user and all their tasks? This action cannot be undone.");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the user
                userService.deleteUser(selectedUser.getId());
                loadAllUsers();
                updateStatistics();
                
                showAlert(Alert.AlertType.INFORMATION, "User Deleted", 
                          "User has been deleted successfully.");
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "User Deletion Failed", 
                          "Could not delete user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleRefreshStatistics() {
        updateStatistics();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
