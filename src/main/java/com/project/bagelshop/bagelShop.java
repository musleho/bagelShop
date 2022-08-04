package com.project.bagelshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class bagelShop extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(bagelShop.class.getResource("bagelShop.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 870, 480);
        stage.setTitle("Sheridan Bagel Shop");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}