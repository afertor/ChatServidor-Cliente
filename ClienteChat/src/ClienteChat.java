import java.io.*;
import java.net.*;

public class ClienteChat {
    public static void main(String[] args) {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Ingrese la dirección IP del servidor: ");
            String serverIP = userInput.readLine();

            System.out.print("Ingrese el puerto del servidor: ");
            int serverPort = Integer.parseInt(userInput.readLine());

            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Conectado al servidor en " + serverIP + ":" + serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Hilo para leer mensajes del servidor
            Thread serverListener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                    System.out.println("El servidor se ha desconectado.");
                } catch (IOException e) {
                    System.err.println("Error al leer del servidor: " + e.getMessage());
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
                    }
                    System.exit(0); // Cerrar el cliente cuando el servidor se desconecta
                }
            });
            serverListener.start();

            // Hilo para enviar mensajes al servidor
            String nickname = getNickname(userInput);
            out.println(nickname);
            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                out.println(userInputMessage);
            }

            // Cierre de recursos
            userInput.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }

    private static String getNickname(BufferedReader userInput) throws IOException {
        System.out.print("Ingrese su nickname: ");
        return userInput.readLine();
    }
}
