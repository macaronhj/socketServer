package com.heejin.socketServer;


import com.heejin.socketServer.model.main.ChattingServer;

public class ChattingServerLauncher {

    public static void main(String[] args) {
        ChattingServer server = new ChattingServer(9100);
        server.start();
    }
}
