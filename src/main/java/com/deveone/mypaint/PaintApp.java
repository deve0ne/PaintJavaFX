package com.deveone.mypaint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PaintApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        final int width = 750;
        final int height = 500;

        FXMLLoader fxmlLoader = new FXMLLoader(PaintApp.class.getResource("paint-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        stage.setMinWidth(width);
        stage.setMinHeight(height);

        stage.setTitle("Paint!");
        stage.getIcons().add(new Image("graphics/icons8-paint-66.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}