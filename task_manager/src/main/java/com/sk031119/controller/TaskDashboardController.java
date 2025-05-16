package com.sk031119.controller;

import com.sk031119.TaskManagerApplication;
import com.sk031119.model.Task;
import com.sk031119.model.User;
import com.sk031119.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class TaskDashboardController implements Initializable {

    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> dueDateColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TextField taskTitleField;
    @FXML private TextArea taskDescriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<Task.Priority> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ToggleGroup taskFilterToggleGroup;
    @FXML private RadioButton allTasksRadio;
    @FXML private RadioButton pendingTasksRadio;
    @FXML private RadioButton completedTasksRadio;
    @FXML private TextField searchField;
    @FXML private Button addTaskButton;
    @FXML private Button updateTaskButton;
    @FXML private Button deleteTaskButton;
    @FXML private Label userLabel; // Added for displaying current user
    @FXML private Button logoutButton; // Added for logout
    @FXML private Button adminPanelButton; // Added for admin access

    private final TaskService taskService;
    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private Task selectedTask;
    private User currentUser;

    @Autowired
    public TaskDashboardController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the current logged-in user
        currentUser = TaskManagerApplication.getCurrentUser();
        
        // Setup user information display
        if (userLabel != null) {
            String roleText = currentUser.isAdmin() ? "Administrator" : "User";
            userLabel.setText("Welcome, " + roleText + " " + currentUser.getName());
            
            // If admin, change the style to highlight admin status
            if (currentUser.isAdmin()) {
                userLabel.setStyle("-fx-text-fill: #b73352; -fx-font-weight: bold;");
            }
        }
        
        setupTableView();
        setupPriorityComboBox();
        setupStatusComboBox();
        setupFilterListeners();
        loadTasksForCurrentUser();
        
        // Initially disable update and delete buttons until selection
        updateTaskButton.setDisable(true);
        deleteTaskButton.setDisable(true);
        
        // Listen for table selection changes
        taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTask = newSelection;
            
            if (newSelection != null) {
                // Populate form fields with selected task
                taskTitleField.setText(newSelection.getTitle());
                taskDescriptionArea.setText(newSelection.getDescription());
                dueDatePicker.setValue(newSelection.getDueDate());
                priorityComboBox.setValue(newSelection.getPriority());
                statusComboBox.setValue(newSelection.isCompleted() ? "Completed" : "Pending");
                
                // Enable update and delete buttons
                updateTaskButton.setDisable(false);
                deleteTaskButton.setDisable(false);
            } else {
                handleClearForm();
                updateTaskButton.setDisable(true);
                deleteTaskButton.setDisable(true);
            }
        });
        
        // Setup search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadTasksBasedOnFilter();
            } else {
                searchTasksForCurrentUser(newValue);
            }
        });
        
        // Setup logout handler if button exists
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> logout());
        }
        
        // Setup admin panel button (only show for admin users)
        if (adminPanelButton != null) {
            if (currentUser.isAdmin()) {
                adminPanelButton.setVisible(true);
                adminPanelButton.setOnAction(e -> openAdminPanel());
            } else {
                adminPanelButton.setVisible(false);
            }
        }
    }

    // New method for handling logout
    @FXML
    private void logout() {
        try {
            // Clear current user
            TaskManagerApplication.setCurrentUser(null);
            // Navigate back to login screen
            TaskManagerApplication.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Could not return to login screen");
        }
    }

    private void setupTableView() {
        // Configure columns
        titleColumn.setCellValueFactory(data -> {
            Task task = data.getValue();
            String title = task.getTitle();
            
            // If admin, prefix tasks that aren't theirs with the owner's name
            if (currentUser.isAdmin() && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
                title = "[" + task.getUser().getUsername() + "] " + title;
            }
            
            return new SimpleStringProperty(title);
        });
        
        dueDateColumn.setCellValueFactory(data -> {
            LocalDate dueDate = data.getValue().getDueDate();
            if (dueDate != null) {
                return new SimpleStringProperty(dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                return new SimpleStringProperty("No due date");
            }
        });
        
        priorityColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPriority().name()));
        
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isCompleted() ? "Completed" : "Pending"));
        
        taskTableView.setItems(taskList);
        
        // Add row styling based on ownership
        taskTableView.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                
                if (task == null || empty) {
                    setStyle("");
                } else {
                    // If admin, highlight tasks belonging to other users
                    if (currentUser.isAdmin() && task.getUser() != null && 
                            !task.getUser().getId().equals(currentUser.getId())) {
                        setStyle("-fx-background-color: rgba(79, 195, 247, 0.2);"); // Light blue background
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
    
    private void setupPriorityComboBox() {
        priorityComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityComboBox.setValue(Task.Priority.MEDIUM); // Default value
    }
    
    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList("Pending", "Completed"));
        statusComboBox.setValue("Pending"); // Default value
    }
    
    private void setupFilterListeners() {
        taskFilterToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            loadTasksBasedOnFilter();
        });
    }
    
    private void loadTasksBasedOnFilter() {
        RadioButton selected = (RadioButton) taskFilterToggleGroup.getSelectedToggle();
        
        if (selected == allTasksRadio) {
            loadTasksForCurrentUser();
        } else if (selected == completedTasksRadio) {
            loadCompletedTasksForCurrentUser();
        } else if (selected == pendingTasksRadio) {
            loadPendingTasksForCurrentUser();
        }
    }
    
    private void loadTasksForCurrentUser() {
        taskList.clear();
        
        // Admin sees all tasks, regular users see only their own
        if (currentUser.isAdmin()) {
            taskList.addAll(taskService.getAllTasks());
        } else {
            taskList.addAll(taskService.getAllTasksForUser(currentUser));
        }
    }
    
    private void loadCompletedTasksForCurrentUser() {
        taskList.clear();
        
        // Admin sees all completed tasks, regular users see only their own
        if (currentUser.isAdmin()) {
            taskList.addAll(taskService.getCompletedTasks());
        } else {
            taskList.addAll(taskService.getCompletedTasksForUser(currentUser));
        }
    }
    
    private void loadPendingTasksForCurrentUser() {
        taskList.clear();
        
        // Admin sees all pending tasks, regular users see only their own
        if (currentUser.isAdmin()) {
            taskList.addAll(taskService.getPendingTasks());
        } else {
            taskList.addAll(taskService.getPendingTasksForUser(currentUser));
        }
    }
    
    private void searchTasksForCurrentUser(String keyword) {
        taskList.clear();
        
        // Admin searches all tasks, regular users search only their own
        if (currentUser.isAdmin()) {
            taskList.addAll(taskService.searchTasks(keyword));
        } else {
            taskList.addAll(taskService.searchTasksForUser(keyword, currentUser));
        }
    }
    
    @FXML
    private void handleAddTask() {
        String title = taskTitleField.getText().trim();
        
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Task title cannot be empty");
            return;
        }
        
        Task newTask = new Task(title);
        newTask.setDescription(taskDescriptionArea.getText());
        newTask.setDueDate(dueDatePicker.getValue());
        newTask.setPriority(priorityComboBox.getValue());
        newTask.setCompleted("Completed".equals(statusComboBox.getValue()));
        
        // Always assign the current user as the owner of a new task
        newTask.setUser(currentUser);
        
        taskService.saveTask(newTask);
        loadTasksBasedOnFilter();
        handleClearForm();
    }
    
    @FXML
    private void handleUpdateTask() {
        if (selectedTask == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a task to update");
            return;
        }
        
        // Check if admin is trying to modify someone else's task
        if (!currentUser.isAdmin() && 
            selectedTask.getUser() != null && 
            !selectedTask.getUser().getId().equals(currentUser.getId())) {
            
            showAlert(Alert.AlertType.ERROR, "Permission Denied", 
                      "You don't have permission to modify this task");
            return;
        }
        
        String title = taskTitleField.getText().trim();
        
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Task title cannot be empty");
            return;
        }
        
        selectedTask.setTitle(title);
        selectedTask.setDescription(taskDescriptionArea.getText());
        selectedTask.setDueDate(dueDatePicker.getValue());
        selectedTask.setPriority(priorityComboBox.getValue());
        selectedTask.setCompleted("Completed".equals(statusComboBox.getValue()));
        
        // Keep the original owner of the task (don't change the user)
        
        taskService.saveTask(selectedTask);
        loadTasksBasedOnFilter();
        handleClearForm();
    }
    
    @FXML
    private void handleDeleteTask() {
        if (selectedTask == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a task to delete");
            return;
        }
        
        // Check if regular user is trying to delete someone else's task
        if (!currentUser.isAdmin() && 
            selectedTask.getUser() != null && 
            !selectedTask.getUser().getId().equals(currentUser.getId())) {
            
            showAlert(Alert.AlertType.ERROR, "Permission Denied", 
                      "You don't have permission to delete this task");
            return;
        }
        
        // Confirm before deleting
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this task?");
        
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            taskService.deleteTask(selectedTask.getId());
            loadTasksBasedOnFilter();
            handleClearForm();
        }
    }
    
    /*
     * Method replaced by status dropdown
     * @FXML
     * private void handleToggleTaskStatus() {
     *     if (selectedTask == null) {
     *         showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a task to toggle its status");
     *         return;
     *     }
     *     
     *     taskService.toggleTaskCompletion(selectedTask.getId());
     *     loadTasksBasedOnFilter();
     * }
     */
    
    @FXML
    private void handleClearForm() {
        clearForm();
    }
    
    private void clearForm() {
        taskTitleField.clear();
        taskDescriptionArea.clear();
        dueDatePicker.setValue(null);
        priorityComboBox.setValue(Task.Priority.MEDIUM);
        statusComboBox.setValue("Pending");
        selectedTask = null;
        taskTableView.getSelectionModel().clearSelection();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to open admin panel
    private void openAdminPanel() {
        try {
            TaskManagerApplication.setRoot("admin-panel");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open admin panel");
        }
    }
}