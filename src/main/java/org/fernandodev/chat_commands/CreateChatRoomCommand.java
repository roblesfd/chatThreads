package org.fernandodev.chat_commands;

import org.fernandodev.ChatRoom;
import org.fernandodev.ChatServer;
import org.fernandodev.ChatServer.ClientHandler;

public class CreateChatRoomCommand implements Command {
    @Override
    public void execute(ClientHandler client, String[] args) {
        if(args.length < 2) {
            client.send("Uso: /crearsala [nombre-sala]");
            return;
        }
        String chatRoomName = args[1];
        if(ChatServer.chatRooms.contains(chatRoomName)){
            client.send("Ya existe una sala con este nombre");
            return;
        }

        ChatServer.chatRooms.put(chatRoomName, new ChatRoom(chatRoomName));
        client.send("Se ha creado la sala");
    }
}
