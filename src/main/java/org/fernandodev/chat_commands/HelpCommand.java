package org.fernandodev.chat_commands;

import org.fernandodev.ChatServer.ClientHandler;

public class HelpCommand implements Command{

    @Override
    public void execute(ClientHandler client, String[] args) {
        String message = """
                Lista de comandos:
            
                /help: Muestra los comandos disponibles.
                /cambiarnombre: Cambia tu nombre de usuario para mostrar.
                /crearsala: Crea una sala de chat para hablar con otras personas.
                /unirse: Entrar a una sala de chat disponible.
                /dejar: Mientras se esta en una sala de chat, abandonarla.
                /conectados: Muestra los usuarios que están conectados.
                """;
        client.send(message);
    }
}