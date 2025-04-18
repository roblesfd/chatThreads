package org.fernandodev.chat_commands;

import org.fernandodev.ChatServer;
import org.fernandodev.ChatServer.ClientHandler;
import org.fernandodev.User;

import java.util.ArrayList;
import java.util.List;

public class ConnectedUsersCommand implements Command{
    @Override
    public void execute(ClientHandler client, String[] args) {
        if(ChatServer.connectedUsers.isEmpty()) {
            client.send("No hay usuarios conectados");
        }else{
            client.send("Lista de usuarios conectados:");
            for(User user: ChatServer.connectedUsers.values()){
                client.send("- " + user.username());
            }
        }
    }
}