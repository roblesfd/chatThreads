package org.fernandodev.chat_commands;

import org.fernandodev.ChatRoom;
import org.fernandodev.ChatServer.ClientHandler;

public class LeaveChatRoomCommand implements Command{
    @Override
    public void execute(ClientHandler client, String[] args) {
        if(client.getCurrentChatRoom() == null) {
            client.send("No estas en ninguna sala actualmente");
            return;
        }

//        ChatRoom room = client.getCurrentChatRoom();
        client.leaveRoom();
        client.send("Has dejado la sala");
    }
}
