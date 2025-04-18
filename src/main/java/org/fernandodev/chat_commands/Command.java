package org.fernandodev.chat_commands;

import org.fernandodev.ChatServer.ClientHandler;

public interface Command {
    void execute(ClientHandler client, String[] args);
}