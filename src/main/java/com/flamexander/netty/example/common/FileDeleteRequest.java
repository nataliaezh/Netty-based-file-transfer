package com.flamexander.netty.example.common;

public class FileDeleteRequest extends AbstractMessage {
    private String filename;

    public String getFilename(){
        return filename;
    }
    public FileDeleteRequest (String filename){
        this.filename=filename;
    }

}
