package com.sk031119.controller;

import com.sk031119.model.Task;
import com.sk031119.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @FXML private ToggleGroup taskFilterToggleGroup;
    @FXML private RadioButton allTasksRadio;
    @FXML private RadioButton pendingTasksRadio;
    @FXML private RadioButton completedTasksRadio;
    @FXML private TextField searchField;
    @FXML private Button addTaskButton;
    @FXML private Button updateTaskButton;
    @FXML private Button deleteTaskButton;

    private final TaskService taskService;
    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private Task selectedTask;

    @Autowired
    public TaskDashboardController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableView();
        setupPriorityComboBox();
        setupFilterListeners();
        loadAllTasks();
        
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
                
                // Enable update and delete buttons
                updateTaskButton.setDisable(false);
                deleteTaskButton.setDisable(false);
            } else {
                clearForm();
                updateTaskButton.setDisable(true);
                deleteTaskButton.setDisable(true);
            }
        });
        
        // Setup search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadTasksBasedOnFilter();
            } else {
                searchTasks(newValue);
            }
        });
    }

    private void setupTableView() {
        // Configure columns
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        dueDateColumn.setCellValueFactory(data -> {
            LocalDate dueDate = data.getValue().getDueDate();
            if (dueDate != null) {
                return new SimpleStringProperty(dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } else {
                return new SimpleStringProperty("No due date");
            }
        });
        
        priorityColumn.setCellValueFactory(data -> {
            Task.Priority priority = data.getValue().getPriority();
            return new SimpleStringProperty(priority != null ? priority.toString() : "MEDIUM");
        });
        
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isCompleted() ? "Completed" : "Pending"));
        
        // Configure row selection
        taskTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Setup double-click to toggle completion
        taskTableView.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Task task = row.getItem();
                    toggleTaskCompletion(task);
                }
            });
            return row;
        });
        
        // Bind the table to our observable list
        taskTableView.setItems(taskList);
    }
    
    private void setupPriorityComboBox() {
        priorityComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        priorityComboBox.setValue(Task.Priority.MEDIUM);
    }
    
    private void setupFilterListeners() {
        allTasksRadio.setUserData("ALL");
        pendingTasksRadio.setUserData("PENDING");
        completedTasksRadio.setUserData("COMPLETED");
        
        taskFilterToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                loadTasksBasedOnFilter();
            }
        });
    }
    
    private void loadTasksBasedOnFilter() {
        Toggle selectedToggle = taskFilterToggleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            return;
        }
        
        String filter = (String) selectedToggle.getUserData();
        switch (filter) {
            case "ALL":
                loadAllTasks();
                break;
            case "PENDING":
                loadPendingTasks();
                break;
            case "COMPLETED":
                loadCompletedTasks();
                break;
        }
    }
    
    private void loadAllTasks() {
        taskList.clear();
        taskList.addAll(taskService.getAllTasks());
    }
    
    private void loadPendingTasks() {
        taskList.clear();
        taskList.addAll(taskService.getPendingTasks());
    }
    
    private void loadCompletedTasks() {
        taskList.clear();
        taskList.addAll(taskService.getCompletedTasks());
    }
    
    private void searchTasks(String keyword) {
        taskList.clear();
        taskList.addAll(taskService.searchTasks(keyword));
    }
    
    @FXML
    private void handleAddTask() {
        if (validateForm()) {
            Task newTask = new Task(
                taskTitleField.getText(), 
                taskDescriptionArea.getText(),
                dueDatePicker.getValue(),
                priorityComboBox.getValue()
            );
            
            taskService.saveTask(newTask);
            clearForm();
            loadTasksBasedOnFilter();
        }
    }
    
    @FXML
    private void handleUpdateTask() {
        if (selectedTask != null && validateForm()) {
            selectedTask.setTitle(taskTitleField.getText());
            selectedTask.setDescription(taskDescriptionArea.getText());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setPriority(priorityComboBox.getValue());
            
            taskService.saveTask(selectedTask);
            clearForm();
            loadTasksBasedOnFilter();
            
            // Clear selection and disable update/delete buttons
            taskTableView.getSelectionModel().clearSelection();
            updateTaskButton.setDisable(true);
            deleteTaskButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleDeleteTask() {
        if (selectedTask != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Task");
            confirmation.setHeaderText("Delete Task Confirmation");
            confirmation.setContentText("Are you sure you want to delete the task: " + selectedTask.getTitle() + "?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    taskService.deleteTask(selectedTask.getId());
                    clearForm();
                    loadTasksBasedOnFilter();
                    
                    // Clear selection and disable update/delete buttons
                    taskTableView.getSelectionModel().clearSelection();
                    updateTaskButton.setDisable(true);
                    deleteTaskButton.setDisable(true);
                }
            });
        }
    }
    
    @FXML
    private void handleClearForm() {
        clearForm();
        taskTableView.getSelectionModel().clearSelection();
        updateTaskButton.setDisable(true);
        deleteTaskButton.setDisable(true);
    }
    
    private void toggleTaskCompletion(Task task) {
        if (task != null) {
            taskService.toggleTaskCompletion(task.getId());
            loadTasksBasedOnFilter();
        }
    }
    
    private boolean validateForm() {
        if (taskTitleField.getText() == null || taskTitleField.getText().trim().isEmpty()) {
            showAlert("Title Required", "Please enter a task title.");
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        taskTitleField.clear();
        taskDescriptionArea.clear();
        dueDatePicker.setValue(null);
        priorityComboBox.setValue(Task.Priority.MEDIUM);
        selectedTask = null;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 