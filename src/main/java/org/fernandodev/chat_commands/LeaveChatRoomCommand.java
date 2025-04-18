package org.fernandodev.chat_commands;

import org.fernandodev.ChatRoom;
import org.fernandodev.ChatRoomHandler;
import org.fernandodev.ChatServer.ClientHandler;

public class LeaveChatRoomCommand implements Command{
    @Override
    public void execute(ClientHandler client, String[] args) {
        if(client.currentChatRoom == null) {
            client.send("No estas en ninguna sala actualmente");
            return;
        }

        ChatRoomHandler.leaveRoom(client, client.user);
        client.send("Has dejado la sala");
    }
}
