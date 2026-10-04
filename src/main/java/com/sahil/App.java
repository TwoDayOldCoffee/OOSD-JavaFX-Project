package com.sahil;

import com.sahil.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/sahil/main.fxml")
        );

        Scene scene = new Scene(loader.load(), 1000, 680);
        MainController mainController = loader.getController();

        // Get the TabPane and attach refresh listeners
        TabPane tabPane = (TabPane) scene.lookup("#mainTabPane");

        for (Tab tab : tabPane.getTabs()) {
            tab.setOnSelectionChanged(e -> {
                if (tab.isSelected()) {
                    mainController.updateFooter();
                }
            });
        }

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}