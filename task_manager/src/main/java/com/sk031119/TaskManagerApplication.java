package com.sk031119;

import atlantafx.base.theme.PrimerLight;
import com.sk031119.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the Task Manager application.
 * Integrates JavaFX with Spring Boot and manages application lifecycle and navigation.
 */
@SpringBootApplication
public class TaskManagerApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(TaskManagerApplication.class.getName());
    
    // View constants to avoid string literals
    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_ADMIN_PANEL = "admin-panel";
    public static final String VIEW_TASK_DASHBOARD = "task-dashboard";
    
    // View dimension constants
    private static final double LOGIN_WIDTH = 500;
    private static final double LOGIN_HEIGHT = 400;
    private static final double ADMIN_PANEL_WIDTH = 700;
    private static final double ADMIN_PANEL_HEIGHT = 500;
    private static final double TASK_DASHBOARD_WIDTH = 900;
    private static final double TASK_DASHBOARD_HEIGHT = 600;
    
    // Default minimum window sizes
    private static final double MIN_WIDTH = 700;
    private static final double MIN_HEIGHT = 500;

    private static Scene scene;
    private static ConfigurableApplicationContext springContext;
    private static User currentUser;

    /**
     * Initializes the Spring application context.
     */
    @Override
    public void init() {
        springContext = SpringApplication.run(TaskManagerApplication.class);
    }

    /**
     * Starts the JavaFX application with the login screen.
     * 
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Apply AtlantaFX theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        try {
            // Start with login screen
            FXMLLoader loader = new FXMLLoader(TaskManagerApplication.class.getResource(VIEW_LOGIN + ".fxml"));
            loader.setControllerFactory(springContext::getBean);
            
            scene = new Scene(loader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);
            stage.setScene(scene);
            stage.setTitle(getViewTitle(VIEW_LOGIN));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load the initial view", e);
            throw e;
        }
    }

    /**
     * Cleans up resources when the application is stopped.
     */
    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    /**
     * Changes the current view to the specified FXML file.
     * 
     * @param viewName The name of the view to load (without .fxml extension)
     * @throws IOException If the FXML file cannot be loaded
     */
    public static void setRoot(String viewName) throws IOException {
        try {
            Parent root = loadFXML(viewName);
            scene.setRoot(root);
            
            Stage stage = (Stage) scene.getWindow();
            configureStageForView(stage, viewName);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load view: " + viewName, e);
            throw e;
        }
    }

    /**
     * Configures the stage properties based on the view being displayed.
     * 
     * @param stage The stage to configure
     * @param viewName The name of the view being displayed
     */
    private static void configureStageForView(Stage stage, String viewName) {
        // Set minimum sizes first
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        
        // Adjust window size based on the view
        switch (viewName) {
            case VIEW_ADMIN_PANEL:
                configureStage(stage, VIEW_ADMIN_PANEL, ADMIN_PANEL_WIDTH, ADMIN_PANEL_HEIGHT);
                break;
            case VIEW_TASK_DASHBOARD:
                configureStage(stage, VIEW_TASK_DASHBOARD, TASK_DASHBOARD_WIDTH, TASK_DASHBOARD_HEIGHT);
                break;
            case VIEW_LOGIN:
                configureStage(stage, VIEW_LOGIN, LOGIN_WIDTH, LOGIN_HEIGHT);
                break;
        }
        
        // Center on screen after resize
        stage.centerOnScreen();
    }
    
    /**
     * Configures stage dimensions and title for a specific view.
     * 
     * @param stage The stage to configure
     * @param viewName The name of the view
     * @param width The preferred width for this view
     * @param height The preferred height for this view
     */
    private static void configureStage(Stage stage, String viewName, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle(getViewTitle(viewName));
    }
    
    /**
     * Gets the formatted window title for a specific view.
     * 
     * @param viewName The name of the view
     * @return The formatted window title
     */
    private static String getViewTitle(String viewName) {
        switch (viewName) {
            case VIEW_ADMIN_PANEL:
                return "Task Manager - Administrator Panel";
            case VIEW_TASK_DASHBOARD:
                return "Task Manager - Dashboard";
            case VIEW_LOGIN:
                return "Task Manager - Login";
            default:
                return "Task Manager";
        }
    }

    /**
     * Loads an FXML file and returns the parent node.
     * 
     * @param fxml The name of the FXML file to load (without .fxml extension)
     * @return The loaded Parent node
     * @throws IOException If the FXML file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TaskManagerApplication.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        return fxmlLoader.load();
    }
    
    /**
     * Gets the current logged-in user.
     * 
     * @return The current User object, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Sets the current logged-in user.
     * 
     * @param user The User object to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}