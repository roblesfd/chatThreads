package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private static Set<ClientHandler> clientes = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executor = Executors.newCachedThreadPool();
        String mensajeInicial = """
                Servidor del chat iniciado en el puerto %s
                """.formatted(PORT);

        System.out.println(mensajeInicial);

        while(true){
            Socket socketClient = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socketClient);
            clientes.add(handler);
            executor.execute(handler);
        }
    }


    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String nombre;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            System.out.println("Nuevo cliente conectado "  + socket);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ){
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);
                nombre = in.readLine();

                String msg;
                while((msg = in.readLine()) != null){
                    String mensajeConNombreUsuario = nombre + ": " + msg;
                    broadcast(mensajeConNombreUsuario);
                }
            }catch(IOException e){
                System.out.println("Error: " + e.getMessage());
            }finally{
                clientWriters.remove(out);
                try{
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void broadcast(String message){
            for(PrintWriter writer :  clientWriters) {
                writer.println(message);
            }
        }
    }
}