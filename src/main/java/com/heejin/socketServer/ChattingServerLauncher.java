package com.heejin.socketServer;


import com.heejin.socketServer.core.ChattingServer;

public class ChattingServerLauncher {

    public static void main(String[] args) {
        ChattingServer server = new ChattingServer(9100);
        server.start();
    }
}
