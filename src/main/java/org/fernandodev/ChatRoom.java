package org.fernandodev;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.fernandodev.ChatServer.ClientHandler;

public class ChatRoom {
    private String name;
    private Set<ClientHandler> members = ConcurrentHashMap.newKeySet();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void addMember(ClientHandler member){
        members.add(member);
        broadcast(member.getUsername() + " se ha unido a la sala.");
    }

    public void removeMember(ClientHandler member){ members.remove(member); }

    public Set<ClientHandler> getMembers() {
        return members;
    }

    public int getNumberOfMembers() { return members.size(); }

    public void broadcast(String message) {
        for(ClientHandler c : members) {
            c.send("[Sala " + name + "] " + message);
        }
    }
}
