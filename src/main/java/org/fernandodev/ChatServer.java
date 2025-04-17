package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private static final Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());
    private static final ConcurrentHashMap<String, User> connectedUsers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
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

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String username;
        private User user;

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

                String msg;
                //Ingresa un mensaje y lo transmite al resto de clients
                while ((msg = in.readLine()) != null) {
                    String mensajeConNombre = username + ": " + msg;
                    broadcast(mensajeConNombre);
                }
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

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
