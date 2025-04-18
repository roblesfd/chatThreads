package org.fernandodev;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record User(
        String username,
        LocalDateTime joinDate,
        LocalDateTime lastConnection,
        Socket socket,
        PrintWriter writer,
        Set<String> chatRooms
) {
    public User(String username, LocalDateTime joinDate, LocalDateTime lastConnection) {
        this(username, joinDate, lastConnection, null, null, new HashSet<>());
    }
    public void addChatRoom(String roomName) {
        chatRooms.add(roomName);
    }

    public void removeChatRoom(String room) {
        chatRooms.remove(room);
    }

    public boolean isInRoom(String roomName) {
        return chatRooms.contains(roomName);
    }

    public User changeUsername(String newName) {
        return new User(newName, this.joinDate(), this.lastConnection(), this.socket(), this.writer(), this.chatRooms());
    }
}