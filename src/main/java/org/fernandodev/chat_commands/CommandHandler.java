package org.fernandodev.chat_commands;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("/help", new HelpCommand());
        commands.put("/cambiarnombre", new ChangeNameCommand());
        commands.put("/crearsala", new CreateChatRoomCommand());
        commands.put("/unirse", new JoinChatRoomCommand());
        commands.put("/dejar", new LeaveChatRoomCommand());
        commands.put("/conectados", new ConnectedUsersCommand());
    }

    public static Command getCommand(String cmdName) {
        return commands.get(cmdName.toLowerCase());
    }
}