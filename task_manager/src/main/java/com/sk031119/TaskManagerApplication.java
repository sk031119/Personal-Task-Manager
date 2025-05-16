package com.sk031119;

import atlantafx.base.theme.PrimerLight;
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

    @Override
    public void init() {
        springContext = SpringApplication.run(TaskManagerApplication.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Apply AtlantaFX theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        // Load the main view
        FXMLLoader loader = new FXMLLoader(TaskManagerApplication.class.getResource("task-dashboard.fxml"));
        loader.setControllerFactory(springContext::getBean);
        
        scene = new Scene(loader.load(), 900, 600);
        stage.setScene(scene);
        stage.setTitle("Personal Task Manager");
        stage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TaskManagerApplication.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
} 