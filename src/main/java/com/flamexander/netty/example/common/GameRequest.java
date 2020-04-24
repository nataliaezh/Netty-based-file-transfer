package com.flamexander.netty.example.common;

public class GameRequest extends AbstractMessage{
    private String gamename;

    public String getGamename() {
        return gamename;
    }

    public GameRequest(String gamename) {
        this.gamename = gamename;
    }
}


