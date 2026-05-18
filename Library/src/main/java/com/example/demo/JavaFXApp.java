package com.example.demo;

import com.example.demo.LibraryApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class JavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load login screen as the first scene
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/login.fxml")
        );

        // Inject Spring beans into JavaFX controllers using the Spring context
        loader.setControllerFactory(
            clazz -> LibraryApplication.springContext.getBean(clazz)
        );

        Scene scene = new Scene(loader.load(), 900, 600);

        primaryStage.setTitle("Digital Library Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Gracefully shut down Spring when JavaFX window closes
        LibraryApplication.springContext.close();
    }
}