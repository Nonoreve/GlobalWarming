package fr.nonoreve.globalWarming;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.nonoreve.globalWarming.model.DataLoader;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent content = FXMLLoader.load(getClass().getResource("gui.fxml"));
            primaryStage.setTitle("Global Warming");
            primaryStage.setScene(new Scene(content));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DataLoader.load("resources/tempanomaly_4x4grid.csv");
        launch(args);
    }
}
