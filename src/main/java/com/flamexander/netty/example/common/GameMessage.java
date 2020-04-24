package com.flamexander.netty.example.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameMessage extends AbstractMessage{
    private String gamename;
    private byte[] dataGame;
    public byte[] getDataGame() {
        return dataGame;
    }

    public String getGamename() {
        return gamename;
    }

    public GameMessage(Path path) throws IOException {
        try{
            gamename = path.getFileName().toString( );
            dataGame = Files.readAllBytes(path);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}