package org.fernandodev;

import org.fernandodev.ChatServer.ClientHandler;

public class ChatRoomHandler {

    public static void sendToCurrentChatRoom(ChatRoomContext context, String message) {
        ChatRoom curRoom = context.getCurrentChatRoom();

        if(curRoom != null) {
            curRoom.broadcast(context.getUsername() + " " + message);
        }else{
            context.getClient().send("No estas en ninguna sala. Usa /entrarsala [sala-nombre]");
        }
    }

    public static void joinRoom(ClientHandler client, ChatRoom room){
        client.currentChatRoom = room;
        room.addMember(client);
    }

    public static void leaveRoom(ClientHandler client, User user){
        if(client.currentChatRoom != null){
            client.currentChatRoom.removeMember(client);
            user.removeChatRoom(client.currentChatRoom.getName());
            client.currentChatRoom = null;
        }
    }
}
