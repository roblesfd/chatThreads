package org.fernandodev;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fernandodev.ChatServer.ClientHandler;

public class ChatRoom {
    private String name;
    private ConcurrentHashMap<String, ClientHandler> members = new ConcurrentHashMap<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void addMember(ClientHandler member){
        members.put(member.username, member);
        broadcast(member.user.username() + " se ha unido a la sala.");
    }

    public void removeMember(ClientHandler member){ members.remove(member); }

    public ConcurrentHashMap<String, ClientHandler> getMembers() {
        return members;
    }

    public void broadcast(String message) {
        for(Map.Entry<String, ClientHandler> c : members.entrySet()) {
            c.getValue().send("[Sala " + name + "] " + message);
        }
    }
}
