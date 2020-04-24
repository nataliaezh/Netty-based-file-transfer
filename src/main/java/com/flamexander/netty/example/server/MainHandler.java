package com.flamexander.netty.example.server;

import com.flamexander.netty.example.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileListRequest) {
                FileListMessage flm = new FileListMessage(getFilesList( ));
                ctx.writeAndFlush(flm);
            }

            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;

                if (Files.exists(
                        Paths.get("server_storage/" + fr.getFilename( )))) {
                    FileMessage fm = new FileMessage(
                            Paths.get("server_storage/" + fr.getFilename( )));
                    ctx.writeAndFlush(fm);
                }
            }

            if (msg instanceof FileDeleteRequest) {
                FileDeleteRequest fdr = (FileDeleteRequest) msg;
                if (Files.exists(Paths.get("server_storage/" + fdr.getFilename( )))) {
                    Files.delete(Paths.get(("server_storage/" + fdr.getFilename( ))));
                    FileListMessage flm = new FileListMessage(getFilesList( ));
                    ctx.writeAndFlush(flm);
                }
            }
            
            if (msg instanceof FileMessage){
                FileMessage fm=(FileMessage) msg;
                uploadFile(fm);
                FileListMessage flm=new FileListMessage(getFilesList());
                ctx.writeAndFlush(flm);
            }

            // игры
            if (msg instanceof GameMessage){
                GameMessage gm=(GameMessage) msg;
                uploadGame(gm);
                GameListMessage glm=new GameListMessage(getGamesList());
                 ctx.writeAndFlush(glm);
            }

            if (msg instanceof GameRequest) {
                GameRequest gr = (GameRequest) msg;


                if (Files.exists(
                        Paths.get("game_storage/" + gr.getGamename()))) {
                    GameMessage gm = new GameMessage(
                            Paths.get("game_storage/" + gr.getGamename()));
                    ctx.writeAndFlush(gm);
                }

                if (msg instanceof GameListRequest) {
                    GameListMessage glm = new GameListMessage(getGamesList());

                    ctx.writeAndFlush(glm);
                    System.out.println("Отправил список игр" );


                }


            }

      
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void uploadGame (GameMessage gm) {
        try {
            Files.write(Paths.get("game_storage/" + gm.getGamename()), gm.getDataGame( ), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }
        private List<String> getGamesList ( ) {
            List <String> games=new ArrayList<>();
            try {
                Files.newDirectoryStream(
                        Paths.get("game_storage/")).forEach(
                        path -> games.add(path.getFileName().toString()));
                System.out.println("Cоздал список игр" );
            }catch (IOException e){
                e.printStackTrace();

            }
            return games;
        }





    private void uploadFile (FileMessage fm) {

        try {
            Files.write(Paths.get("server_storage/" + fm.getFilename( )), fm.getData( ), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

    private List<String> getFilesList ( ) {
    List <String> files=new ArrayList<>();
                try {
            Files.newDirectoryStream(
                    Paths.get("server_storage/")).forEach(
                            path -> files.add(path.getFileName().toString()));
            }catch (IOException e){
                    e.printStackTrace();

                }
                return files;
        }



    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace( );
        ctx.close( );
    }
}



