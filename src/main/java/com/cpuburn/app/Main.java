package com.cpuburn.app;

import com.cpuburn.controller.MainController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/main.fxml")
        );

        Parent root = loader.load();
        MainController controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );

        // Garante shutdown correto ao fechar a janela
        stage.setOnCloseRequest(e -> {
            controller.shutdown();
            Platform.exit();
            System.exit(0);
        });

        stage.setTitle("CPU Burn — Monitor de Threads");
        stage.setScene(scene);
        stage.show();
    }
}