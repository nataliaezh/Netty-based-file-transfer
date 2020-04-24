package com.flamexander.netty.example.client;


import com.flamexander.netty.example.common.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;

public class MainController implements Initializable {

    @FXML
    TextArea textArea;

    @FXML
    TextField tfFileName, tfGameName;

    @FXML
    private ListView<String> filesListRemote;
    @FXML
    private ListView<String> filesListLocal;

    @FXML
    private ListView<String> filesListGame;

    @FXML
    TextField msgField;
    @FXML
    TextField loginField;

    @FXML
    HBox authPanel, msgPanel, gamePanel;
    @FXML
    PasswordField passField;
    @FXML
    VBox rootNode;
    private boolean authentificated;

    private boolean connectedToGame = false;
    private boolean returnToMenu;


    public int id;
    private Network network;


    public void setAuthentificated (boolean authentificated) {
        this.authentificated = authentificated;
        this.authPanel.setVisible(!authentificated);
        this.authPanel.setManaged(!authentificated);
        this.msgPanel.setVisible(authentificated);
        this.msgPanel.setManaged(authentificated);
        this.gamePanel.setVisible(connectedToGame);
        this.gamePanel.setManaged(connectedToGame);

    }

    public void setConnectedToGame (boolean connectedToGame) {
        this.connectedToGame = connectedToGame;
        this.msgPanel.setVisible(!connectedToGame);
        this.msgPanel.setManaged(!connectedToGame);
        this.gamePanel.setVisible(connectedToGame);
        this.gamePanel.setManaged(connectedToGame);

    }

    public void setReturnToMenu (boolean returnToMenu) {
        this.returnToMenu = returnToMenu;
        this.gamePanel.setVisible(!returnToMenu);
        this.gamePanel.setManaged(!returnToMenu);
        this.msgPanel.setVisible(returnToMenu);
        this.msgPanel.setManaged(returnToMenu);


    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
       textArea.appendText("Список настольных игр для скачивания: game.txt");


        initializeSceneStyle( );

        this.setAuthentificated(false);

        Network.start( );
        try {
            refreshLocalFiles( );

        } catch (IOException e) {
            e.printStackTrace( );
        }
        Thread t = new Thread(( ) -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject( );//получение файлов на сервере
                    if (am instanceof FileListMessage) {
                        FileListMessage fir = (FileListMessage) am;
                        // Files.write(Paths.get("client_storage/" + fm.getFilename( )), fm.getData( ), StandardOpenOption.CREATE);
                        refreshServerFilesList(fir);
                    }
                    if (am instanceof GameListMessage) {
                        GameListMessage gir =(GameListMessage) am;
                        // Files.write(Paths.get("client_storage/" + fm.getFilename( )), fm.getData( ), StandardOpenOption.CREATE);
                        refreshGameList(gir);

                    }
                    if (am instanceof FileMessage) { //прием файла с сервера
                        FileMessage fm = (FileMessage) am;
                        // Files.write(Paths.get("client_storage/" + fm.getFilename( )), fm.getData( ), StandardOpenOption.CREATE);
                        downloadFile(fm);
                        refreshLocalFiles( );
                    }
                    //игры
                    if (am instanceof GameMessage) { //прием игры с сервера
                        GameMessage gm = (GameMessage) am;
                        downloadGame(gm);
                        refreshLocalFiles( );
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace( );
            } finally {
                Network.stop( );
            }
        });
        t.setDaemon(true);
        t.start( );

    }

    public void initializeSceneStyle ( ) {
        Platform.runLater(new Runnable( ) {
            @Override
            public void run ( ) {
                authPanel.setPadding(new Insets(20, 20, 20, 20));
                authPanel.getChildren( ).get(0).setEffect(new DropShadow(200, Color.BLACK));


            }
        });
    }


    private void downloadFile (FileMessage fm) {
        if (Platform.isFxApplicationThread( )) {
            saveFile(fm);
        } else {
            Platform.runLater(( ) -> saveFile(fm));
        }
    }

    private boolean saveFile (FileMessage fm) {

        try {
            Files.write(Paths.get("local_storage/" + fm.getFilename( )), fm.getData( ), StandardOpenOption.CREATE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setHeaderText(filesListRemote.getSelectionModel( ).getSelectedItem( ));
            alert.setContentText("File downloaded");
            alert.showAndWait( );
            return true;
        } catch (IOException e) {
            e.printStackTrace( );
        }
        return false;
    }


    public void downloadButton (ActionEvent actionEvent) {

        Network.sendMsg(
                new FileRequest(filesListRemote.getSelectionModel( ).getSelectedItem( )));
    }

    @FXML
    private void refreshServerFilesList (FileListMessage flm) {
        if (Platform.isFxApplicationThread( )) {
            filesListRemote.getItems( ).clear( );
            flm.getFiles( ).forEach(s -> filesListRemote.getItems( ).add(s));
        } else {
            Platform.runLater(( ) -> {
                filesListRemote.getItems( ).clear( );
                flm.getFiles( ).forEach(s -> filesListRemote.getItems( ).add(s));
            });
        }
    }

    public void deleteButton (ActionEvent actionEvent) {
        Network.sendMsg(new FileDeleteRequest(filesListRemote.getSelectionModel( ).getSelectedItem( )));
    }


    public void refreshLocalButton (ActionEvent actionEvent) throws IOException {
        refreshLocalFiles( );
    }


    private void refreshLocalFiles ( ) throws IOException {
        ObservableList<String> list = FXCollections.observableArrayList( );

        if (Platform.isFxApplicationThread( )) {
            filesListLocal.getItems( ).clear( );
            Files.list(Paths.get("local_storage")).map(p -> p.getFileName( ).toString( )).forEach(o -> list.add(o));
            filesListLocal.setItems(list);

        } else {
            Platform.runLater(( ) -> {
                filesListLocal.getItems( ).clear( );
                try {
                    Files.list(Paths.get("local_storage")).map(p -> p.getFileName( ).toString( )).forEach(o -> list.add(o));
                } catch (IOException e) {
                    e.printStackTrace( );
                }
                filesListLocal.setItems(list);
            });
        }
    }


    public void refreshServerButton (ActionEvent actionEvent) {
        Network.sendMsg(new FileListRequest( ));

    }

    public void deleteLocalFileButton (ActionEvent actionEvent) throws IOException {
        if (Platform.isFxApplicationThread( )) {
            Files.delete(Paths.get("local_storage/" + filesListLocal.getSelectionModel( ).getSelectedItem( )));
        } else {
            Platform.runLater(( ) -> {
                try {
                    Files.delete(Paths.get("local_storage/" + filesListLocal.getSelectionModel( ).getSelectedItem( )));
                } catch (IOException e) {
                    e.printStackTrace( );
                }
            });
        }
        refreshLocalFiles( );
    }

    public void uploadButton (ActionEvent actionEvent) throws IOException {
        Network.sendMsg(new FileMessage(Paths.get("local_storage/" + filesListLocal.getSelectionModel( ).getSelectedItem( ))));
    }


    public void authentification (ActionEvent actionEvent) {
        System.out.println(loginField.getText( ) + " " + passField.getText( ));
        System.out.println("id = " + id);
        this.setAuthentificated(true);
        //showWelcome( );
    }


    public void connectButton (ActionEvent actionEvent) {
        connectedToGame = true;
        setConnectedToGame(connectedToGame);
    }


    //Игры


    public void downloadGameButton (ActionEvent actionEvent) {
        if (tfFileName.getLength( ) > 0) {
            Network.sendMsg(new GameRequest(tfFileName.getText( )));
            tfFileName.clear( );
            //   showAlert( );
        }
    }


    @FXML
    private void refreshGameList (GameListMessage glm) throws IOException{
        if (Platform.isFxApplicationThread( )) {
            filesListGame.getItems( ).clear( );
            glm.getGames( ).forEach(s -> filesListGame.getItems( ).add(s));
            System.out.println("заполнил лист" );
        } else {
            Platform.runLater(( ) -> {
                filesListGame.getItems( ).clear( );
                glm.getGames( ).forEach(s -> filesListGame.getItems( ).add(s));
            });
        }
    }


    private void downloadGame (GameMessage gm) {
        if (Platform.isFxApplicationThread( )) {
            saveGame(gm);
        } else {
            Platform.runLater(( ) -> saveGame(gm));
        }
    }
/*
    public void pressOnDownloadBtn (ActionEvent actionEvent) {
        if (tfGameName.getLength( ) > 0) {
            Network.sendMsg(new GameRequest(tfGameName.getText( )));
            tfGameName.clear( );
            // showAlert( );
        }
    }

 */

    private boolean saveGame (GameMessage gm) {

        try {
            Files.write(Paths.get("local_storage/" + gm.getGamename( )), gm.getDataGame( ), StandardOpenOption.CREATE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            alert.setHeaderText(filesListGame.getSelectionModel( ).getSelectedItem( ));
            alert.setContentText("Game downloaded");
            alert.showAndWait( );
            return true;
        } catch (IOException e) {
            e.printStackTrace( );
        }
        return false;
    }

    public void refreshGameButton (ActionEvent actionEvent) {
        Network.sendMsg(new GameListRequest( ));
        System.out.println("Послал запрос на список игр" );

    }

    public void connectMenu (ActionEvent actionEvent) {
        returnToMenu = true;
        setReturnToMenu(returnToMenu);
    }


    public void btnShow2SceneStage (ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass( ).getResource("/Scene1.fxml"));
            Stage stage = new Stage( );
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

/*
        private void showAlert ( ) {
            GuiHelper.updateUI(( ) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Файл загружен");
                alert.setHeaderText(null);
                //alert.setContentText(msg);
                alert.showAndWait();
            });
        }

 */
}






