package org.fernandodev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static String serverAddress = "localhost";
    private static int port = 12345;
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
            String msgBienvenida = """
                    Conectado al servidor del chat
                    'CTRL + X' y ENTER para terminar la conexión
                    """;
            System.out.println(msgBienvenida);
            out.println(nombre); // <- Envia el nombre al servidor una sola vez

            //Listener para recibir mensajes de otros clientes (sockets)
            Thread listener = new Thread(() -> {
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
            });
            listener.start();

            //Ingresa un mensaje y lo envia
            String msg;
            while ((msg = input.readLine()) != null) {
                if(msg.length() == 1 && msg.charAt(0) == 24){
                    System.out.println("Desconectándose del servidor...");
                    out.println(msg);
                    socket.close();
                    break;
                }
                out.println(msg);
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        String name = (args.length > 0) ? args[0] : "Anónimo";
        ChatClient client1 = new ChatClient(name);
        client1.connect();
    }
}
