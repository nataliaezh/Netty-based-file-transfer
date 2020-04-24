package com.flamexander.netty.example.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
public class Scene1Controller {
    @FXML
    VBox rootNode;

    @FXML
    HBox msgPanel;

    public void start (ActionEvent actionEvent) {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Scene1.fxml"));
            Image wall = new Image("scene1.png");
            ImageView wallIV= new ImageView(wall);
            rootNode.getChildren().add(wallIV);
            Scene scene = new Scene(root);
            ((Stage)rootNode.getScene().getWindow()).setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initializeSceneStyle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                msgPanel.setPadding(new Insets(20, 20, 20, 20));
                msgPanel.getChildren().get(0).setEffect(new DropShadow(200, Color.BLACK));
                Image wall = new Image("scene1.png");
                ImageView wallIV= new ImageView(wall);
                rootNode.getChildren().add(wallIV);

            }
        });
    }

}
