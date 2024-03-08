import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorChat {
    private static final int MAX_CLIENTS = 10;
    private static int connectedClients = 0;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el puerto para iniciar el servidor: ");
        int port = scanner.nextInt();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado en el puerto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (connectedClients < MAX_CLIENTS) {
                    connectedClients++;
                    System.out.println("Nuevo cliente conectado. Actualmente hay " + connectedClients + " usuarios conectados.");

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                } else {
                    System.out.println("Número máximo de clientes alcanzado. Rechazando la conexión.");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            closeServer();
            scanner.close();
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            } else {
                System.out.println(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        connectedClients--;
        System.out.println(client.getNickname() + " se ha desconectado.");
        broadcastMessage("Cliente " + client.getNickname() + " se ha desconectado.", null);
        if (connectedClients == 0) {
            System.out.println("Ningún cliente conectado.");
            shutdown();
        }
    }

    private static void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                for (ClientHandler client : clients) {
                    client.sendMessage("El servidor se ha desconectado.");
                    client.closeSocket();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    private static void shutdown() {
        System.out.println("El servidor se ha desconectado.");
        System.exit(0);
    }
}

