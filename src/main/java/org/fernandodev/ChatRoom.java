package org.fernandodev;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private String name;
    private Set<PrintWriter> members;

    public ChatRoom(String name) {
        this.name = name;
        this.members = Collections.synchronizedSet(new HashSet<>());
    }

    public String getName() { return name; }

    public void addMember(PrintWriter member){ members.add(member); }

    public void removeMember(PrintWriter member){ members.remove(member); }

    public void sendMessage(String message) {
        synchronized(members){
            for(PrintWriter member : members) {
                member.println(message);
            }
        }
    }

    public int getNumberOfMembers() { return members.size(); }
}
