package com.flamexander.netty.example.client;

import javafx.application.Platform;

public class GuiHelper {
    public static void updateUI(Runnable r){
        if (Platform.isFxApplicationThread()){
            r.run();
        }else{
            Platform.runLater(r);
        }
    }
}
