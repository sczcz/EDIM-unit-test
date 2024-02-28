package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class creates a thread pool and handles the communication from the Client.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ReceiverServer extends Thread {
    private ServerController serverController;
    private ServerSocket serverSocket;
    private int port;

    /**
     * Receives all necessary data and starts the server and then generates and starts the thread pool.
     *
     * @param port          received port number.
     */
    public ReceiverServer(int port, ServerController serverController) {
        this.port = port;
        this.serverController = serverController;
        startServer();
        this.start();
    }

    /**
     * Creates a server socket.
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a connection and creates the streams and sends it forth to the ClientHandler class.
     */
    public void run() {
        while (!Thread.interrupted()) {
            Socket socket;
            try {
                System.out.println("RS: Lyssnar efter anslutningar"); //TODO: Test - ta bort
                socket = serverSocket.accept();
                System.out.println("RS: Fått en ny anslutning");  //TODO: Test - ta bort
                new ConnectionStream(socket,serverController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
