package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String serverAddress = "localhost";
    private static final int port = 12345;
    private String nombre;

    public ChatClient(String name) {
        this.nombre = name;
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
                listenIncomingMessages(in);
            });
            listener.start();

            //Ingresa un mensaje y lo envia
            String msgToSend;
            while ((msgToSend = input.readLine()) != null) {
                //Si se ingreso CTRL + X
                if(msgToSend.length() == 1 && msgToSend.charAt(0) == 24){
                    System.out.println("Desconectándose del servidor...");
                    out.println(msgToSend);
                    socket.close();
                    break;
                }else if("/crearsala".equals(msgToSend)){
                    System.out.println("Se ha creado una sala");
                }else{
                    out.println(msgToSend);
                }
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showWelcomeMessage(PrintWriter out) {
        String msgBienvenida = """
                    Conectado al servidor del chat
                    'CTRL + X' y ENTER para terminar la conexión
                    """;
        System.out.println(msgBienvenida);
        out.println(nombre); // <- Envia el nombre del usuario al servidor una sola vez
    }

    private void listenIncomingMessages(BufferedReader in){
        String msgFromServer;
        try {
            while ((msgFromServer = in.readLine()) != null) {
                String mensajeFormateado = msgFromServer.startsWith(nombre + ":") ?
                        Colores.VERDE + msgFromServer + Colores.RESET :
                        Colores.CIAN + msgFromServer + Colores.RESET;
                System.out.println(mensajeFormateado);
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada");
        }
    }

    public static void main(String[] args) throws IOException {
        String name = (args.length > 0) ? args[0] : "Anónimo";
        ChatClient client1 = new ChatClient(name);
        client1.connect();
    }
}
