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

@SpringBootApplication
public class TaskManagerApplication extends Application {

    private static Scene scene;
    private static ConfigurableApplicationContext springContext;
    private static User currentUser;

    @Override
    public void init() {
        springContext = SpringApplication.run(TaskManagerApplication.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Apply AtlantaFX theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        // Start with login screen
        FXMLLoader loader = new FXMLLoader(TaskManagerApplication.class.getResource("login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        
        scene = new Scene(loader.load(), 500, 400);
        stage.setScene(scene);
        stage.setTitle("Task Manager - Login");
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        
        // Update window size based on preferred dimensions of the new view
        Stage stage = (Stage) scene.getWindow();
        
        // Set minimum sizes first
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        
        // Adjust window size based on the FXML preferred size
        if ("admin-panel".equals(fxml)) {
            stage.setWidth(700);
            stage.setHeight(500);
            stage.setTitle("Task Manager - Administrator Panel");
        } else if ("task-dashboard".equals(fxml)) {
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setTitle("Task Manager - Dashboard");
        } else if ("login".equals(fxml)) {
            stage.setWidth(500);
            stage.setHeight(400);
            stage.setTitle("Task Manager - Login");
        }
        
        // Center on screen after resize
        stage.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TaskManagerApplication.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        return fxmlLoader.load();
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void main(String[] args) {
        launch();
    }
}