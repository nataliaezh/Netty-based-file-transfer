package com.flamexander.netty.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Дополнительные материалы для уровня Pre-Int");
        Scene scene = new Scene(root,600, 600);

        // root.setStyle("-fx-background-image: url(/com/sun/javafx/scene/control/skin/modena/HTMLEditor-Paste.png);");


        primaryStage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.show();



    }

    public static void main (String[] args) throws Exception {
        launch(args);

        // выведет в консоль список файлов
        /*
    List<String> fileList = Files
                .list(Paths.get("game_storage"))
                //показывает список файлов вне директории
                .filter(path-> Files.isDirectory(path))
                .map(path->path.getFileName().toString())
                .collect(Collectors.toList());
        System.out.println(fileList);

         */
    }
}

