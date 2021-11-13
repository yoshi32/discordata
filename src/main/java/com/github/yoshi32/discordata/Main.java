package com.github.yoshi32.discordata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double x;
    private double y;


    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.setProperty("prism.lcdtext", "false");

        stage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
        VBox vBox = loader.load();

        vBox.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        vBox.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);
        });

        primaryStage.setScene(new Scene(vBox));
        primaryStage.setTitle("Test");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}