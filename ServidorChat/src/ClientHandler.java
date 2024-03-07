import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            nickname = in.readLine();
            System.out.println(nickname + " se ha conectado a la sala de chat.");
            ServidorChat.broadcastMessage("Nuevo cliente conectado: " + nickname, this);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("/bye".equals(inputLine)) {
                    break;
                }
                ServidorChat.broadcastMessage(nickname + ": " + inputLine, this); // Reenvía el mensaje a todos los clientes excepto al que lo envió
            }
        } catch (IOException e) {
            System.err.println("Error al manejar el cliente: " + e.getMessage());
        } finally {
            ServidorChat.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
        }
    }

    public String getNickname() {
        return nickname;
    }
}

