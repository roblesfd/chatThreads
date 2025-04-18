package org.fernandodev.chat_commands;

import org.fernandodev.ChatServer;
import org.fernandodev.ChatServer.ClientHandler;

public class StartPrivateChatCommand implements Command {
    @Override
    public void execute(ClientHandler client, String[] args) {
        if(args.length < 2){
            client.send("Uso del comando: /privado [nombre-usuario]");
            return;
        }

        if(client.currentChatRoom == null){
            client.send("Debes estar en una sala para poder iniciar un chat privado.");
            return;
        }

        String username = args[1];
        if(!ChatServer.connectedUsers.containsKey(username)){
            client.send("El usuario no existe o no esta conectado");
            return;
        }

        //Inicia chat privado

    }
}
