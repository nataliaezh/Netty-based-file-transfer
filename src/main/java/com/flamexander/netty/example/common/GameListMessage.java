package com.flamexander.netty.example.common;

import java.util.List;

public class GameListMessage extends AbstractMessage {
    private List<String> games;

    public  List<String> getGames(){
        return games;
    }

    public  GameListMessage (List<String> games){
        this.games=games;
    }
}
