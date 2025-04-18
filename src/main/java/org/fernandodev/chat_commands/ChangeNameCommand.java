package org.fernandodev.chat_commands;

import org.fernandodev.ChatServer;
import org.fernandodev.ChatServer.ClientHandler;

public class ChangeNameCommand implements Command {
    @Override
    public void execute(ClientHandler client, String[] args) {
        if (args.length < 2) {
            client.send("Uso: /cambiarnombre [nuevo-nombre]");
            return;
        }

        String oldName = client.user.username();
        String newName = args[1];

        if (ChatServer.connectedUsers.containsKey(newName)) {
            client.send("Ya existe un usuario con ese nombre.");
            return;
        }

        ChatServer.connectedUsers.remove(oldName);
        ChatServer.connectedUsers.put(newName, client.user.changeUsername(newName));

        client.setUsername(newName);
        client.send("Tu nombre ha sido cambiado a " + newName);
        ChatServer.broadcastGlobal(oldName + " ahora se llama " + newName);
    }
}
