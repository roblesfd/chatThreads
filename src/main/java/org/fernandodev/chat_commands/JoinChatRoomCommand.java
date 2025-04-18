package org.fernandodev.chat_commands;

import org.fernandodev.ChatRoom;
import org.fernandodev.ChatServer;
import org.fernandodev.ChatServer.ClientHandler;

public class JoinChatRoomCommand implements Command{

    @Override
    public void execute(ClientHandler client, String[] args) {
        if(args.length < 2) {
            client.send("Uso: /unirsesala [nombre-sala]");
            return;
        }

        String chatRoomName = args[1];
        ChatRoom room = ChatServer.chatRooms.get(chatRoomName);

        if(room != null){
            client.leaveRoom();
            client.joinRoom(room);
            client.send("Te has unido a la sala " + chatRoomName);
        }else{
            client.send("No existe una sala con el nombre " + chatRoomName);
        }
    }
}
