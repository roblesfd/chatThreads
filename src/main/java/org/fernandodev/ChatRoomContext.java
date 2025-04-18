package org.fernandodev;

import org.fernandodev.ChatServer.ClientHandler;

//Objeto que almacena campos relevantes para enviar mensajes a una sala
public class ChatRoomContext {
    private final String username;
    private final ChatRoom currentChatRoom;
    private final ClientHandler client;

    public ChatRoomContext(String username, ChatRoom currentChatRoom, ClientHandler client) {
        this.username = username;
        this.currentChatRoom = currentChatRoom;
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public ChatRoom getCurrentChatRoom() {
        return currentChatRoom;
    }

    public ClientHandler getClient() {
        return client;
    }
}
