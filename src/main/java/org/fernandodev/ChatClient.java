package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String serverAddress = "localhost";
    private static final int port = 12345;
    private String username;

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

    public void obtainUsername(){
        Scanner read = new Scanner(System.in);
        System.out.println("Ingresa un nombre de usuario para comenzar");
        String name = read.nextLine();
        this.username = name;
    }

    private void showWelcomeMessage(PrintWriter out) {
        String msgBienvenida = """
                    Conectado al servidor principal
                    'CTRL + X' y ENTER para terminar la conexión
                    
                    /help Para mostrar comandos disponibles.
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

    public void sendMessageListener(BufferedReader input, PrintWriter out, Socket socket) throws IOException {
        String msgToSend;
        while ((msgToSend = input.readLine()) != null) {
            //Si se ingreso CTRL + X
            if(msgToSend.length() == 1 && msgToSend.charAt(0) == 24){
                System.out.println("Desconectándose del servidor...");
                socket.close();
                break;
            //Si solo fue un texto
            }else{
                out.println(msgToSend);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatClient client1 = new ChatClient();
        client1.obtainUsername();
        client1.connect();
    }
}
