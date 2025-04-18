package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String serverAddress = "localhost";
    private static final int port = 12345;
    private String username;

    public ChatClient(String name) {
        this.username = name;
    }

    public void connect()  {
        try (
                Socket socket = new Socket(serverAddress, port);
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {

            showWelcomeMessage(out);

            //Listener para recibir mensajes de otros clientes (sockets)
            Thread listener = new Thread(() -> {
                incomingMessageListener(in);
            });
            listener.start();

            //Ingresa un mensaje y lo envia
            sendMessageListener(input, out, socket);

        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showWelcomeMessage(PrintWriter out) {
        String msgBienvenida = """
                    Conectado al servidor principal
                    'CTRL + X' y ENTER para terminar la conexión
                    
                    Lista de Comandos:
                    
                    /help: Muestra los comandos disponibles.
                    /cambiarnombre: Cambia tu nombre de usuario para mostrar.
                    /crearsala: Crea una sala de chat para hablar con otras personas.
                    /unirsesala: Entrar a una sala de chat disponible.
                    /dejarsala: Mientras se esta en una sala de chat, abandonarla.
                    """;
        System.out.println(msgBienvenida);
        out.println(username); // <- Envia el nombre del usuario al servidor una sola vez
    }

    private void incomingMessageListener(BufferedReader in){
        String msgFromServer;
        try {
            while ((msgFromServer = in.readLine()) != null) {
                String mensajeFormateado = msgFromServer.startsWith(username + ":") ?
                        Colores.VERDE + msgFromServer + Colores.RESET :
                        Colores.CIAN + msgFromServer + Colores.RESET;
                System.out.println(mensajeFormateado);
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada");
        }
    }

    public void setUsername(String newName) { this.username = newName; }

    public void sendMessageListener(BufferedReader input, PrintWriter out, Socket socket) throws IOException {
        String msgToSend;
        while ((msgToSend = input.readLine()) != null) {
            //Si se ingreso CTRL + X
            if(msgToSend.length() == 1 && msgToSend.charAt(0) == 24){
                System.out.println("Desconectándose del servidor...");
                out.println(msgToSend);
                socket.close();
                break;
            //Si solo fue un texto
            }else{
                out.println(msgToSend);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String name = (args.length > 0) ? args[0] : "Anónimo";
        ChatClient client1 = new ChatClient(name);
        client1.connect();
    }
}
