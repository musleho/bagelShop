package com.project.bagelshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class bagelShop extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(bagelShop.class.getResource("bagelShop.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 870, 480);
        stage.setTitle("Sheridan Bagel Shop");
        stage.getIcons().add(new Image("https://rs-menus-api.roocdn.com/images/2a2a6824-2eb2-4d24-8a02-995e57466bd9/image.jpeg?width=1200&height=630&auto=webp&format=jpg&fit=crop&v="));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}