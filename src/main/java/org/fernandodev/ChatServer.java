package org.fernandodev;

import org.fernandodev.chat_commands.Command;
import org.fernandodev.chat_commands.CommandHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private static final Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());
    public static final ConcurrentHashMap<String, User> connectedUsers = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private static volatile boolean running = true;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executor = Executors.newCachedThreadPool();

        System.out.println("Servidor del chat iniciado en el puerto " + PORT);
        System.out.println("Presiona Ctrl + X para cerrar el servidor");

        // Hilo para cerrar socket server con Ctrl + X
        Thread socketServerControl = new Thread(() -> {
            ChatServer.closeServerSocketHandler(serverSocket);
        });
        socketServerControl.setDaemon(true);
        socketServerControl.start();

        //Agregar y gestionar socket clients
        ChatServer.socketClientHandler(serverSocket, executor);

        // Apagar el executor y cerrar los clientes
        ChatServer.postCloseOperations(executor);
    }

    private static void closeServerSocketHandler(ServerSocket serverSocket) {
        try {
            while (true) {
                int key = System.in.read();
                if (key == 24) { // Ctrl + X
                    System.out.println("Cerrando el servidor");
                    running = false;
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        System.out.println("Error cerrando ServerSocket: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error en el hilo de control: " + e.getMessage());
        }
    }

    private static void socketClientHandler(ServerSocket serverSocket, ExecutorService executor) {
        while (running) {
            try {
                Socket socketClient = serverSocket.accept();
                clientSockets.add(socketClient);
                executor.execute(new ClientHandler(socketClient));
            } catch (IOException e) {
                if (!running) break;
                System.out.println("Error aceptando conexión: " + e.getMessage());
            }
        }
    }

    private static void postCloseOperations(ExecutorService executor) {
        executor.shutdownNow();
        clientWriters.clear();
        for (Socket s : clientSockets) {
            try {
                s.close();
            } catch (IOException ignored) {}
        }

        System.out.println("Conexión del servidor terminada.");
    }

    public static void broadcastGlobal(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    public static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        public String username;
        public User user;
        public ChatRoom currentChatRoom;
        private String privateChatWith; //nombre de usuario

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Nuevo cliente conectado: " + socket);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);

                username = in.readLine();
                user = new User(
                        username,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        socket,
                        out,
                        new HashSet<>()
                );
                broadcast(username + " se ha conectado");
                connectedUsers.put(username, user);

                //Ingresa un mensaje y lo transmite al resto de clientes
                sendMessageListener(in);

            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + username);
            } finally {
                clientWriters.remove(out);
                clientSockets.remove(socket);
                connectedUsers.remove(username);
                broadcast(username + " se ha desconectado");
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error cerrando socket cliente: " + e.getMessage());
                }
            }
        }

        private void sendMessageListener(BufferedReader in) throws IOException {
            String msg;
            //Ingresa un mensaje y lo transmite al resto de clients
            while ((msg = in.readLine()) != null) {
                //Si es un comando
                if(msg.startsWith("/")){
                    processCommand(msg);
                }else if(currentChatRoom != null){
                    ChatRoomContext context = new ChatRoomContext(username, currentChatRoom, this);
                    ChatRoomHandler.sendToCurrentChatRoom(context, msg);
                }
            }
        }

        private void processCommand(String msg) {
            String[] parts = msg.split(" ");
            String commandName = parts[0];
            Command command = CommandHandler.getCommand(commandName);
            if(command != null) {
                command.execute(this, parts);
            }else{
                send("Comando no reconocido: " + commandName);
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }

        public void setUsername(String newName) {
            this.username = newName;
            this.user = this.user.changeUsername(newName); // Actualiza internamente también
        }

        public void send(String message) {
            out.println(message);
        }

    }
}
