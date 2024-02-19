package server;

import shared.Buffer;
import shared.User;
import shared.UserType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class creates a thread pool and handles the communication from the Client.
 *
 * @version 1.0
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 */

public class ReceiverServer {
    private ServerSocket serverSocket;
    private int port;
    private String className = "Class: ReceiverServer ";
    private LinkedList<ReceiverThread> threadPool;
    private Map<String, SocketStreamObject> socketHashMap;
    private Buffer receiveBuffer;

    /**
     * Receives all necessary data and starts the server and then generates and starts the thread pool.
     *
     * @param port          received port number.
     * @param socketHashMap received socket HashMap.
     * @param receiveBuffer received buffer.
     */
    public ReceiverServer(int port, HashMap<String, SocketStreamObject> socketHashMap, Buffer receiveBuffer) {
        this.port = port;
        this.socketHashMap = socketHashMap;
        this.threadPool = new LinkedList<>();
        this.receiveBuffer = receiveBuffer;
        startServer();
        generateThreadPool(1);
        startThreadPool();
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
     * Generates a thread pool that adds as many ReceiverThreads with the argument.
     *
     * @param nbrOfConnections received number of connections.
     */
    public void generateThreadPool(int nbrOfConnections) {
        for (int i = 0; i < nbrOfConnections; i++) {
            threadPool.add(new ReceiverThread());
        }
    }

    /**
     * Starts the thread pool.
     */
    public void startThreadPool() {
        for (ReceiverThread thread : threadPool)
            thread.start();
    }

    // Inner Thread class: creates a connection and sends it forth to the ClientHandler class.
    private class ReceiverThread extends Thread {
        private String className = "Class: ReceiverThread ";

        /**
         * creates a connection and creates the streams and sends it forth to the ClientHandler class.
         */
        public void run() {
            while (!Thread.interrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    new ClientHandler(socket, ois, oos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Inner Thread Class: creates the streams and receives data from the client.
    private class ClientHandler extends Thread {
        private SocketStreamObject socketStreamObject;
        private String className = "Class: ClientHandler ";
        private volatile boolean running = true;
        private ObjectInputStream ois;

        /**
         * Creates a Socket Stream Object with the received socket.
         *
         * @param socket the received object.
         */
        public ClientHandler(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
            socketStreamObject = new SocketStreamObject(socket, ois, oos);
            this.ois = ois;
            start();
        }

        /**
         * Requirement: K.G.2
         * Reads an object from the input stream and checks its value and send it to {@link ServerController}.
         */
        public void run() {
            User user;
            UserType userType;
            String userName = "";

            while (running) {
                try {
                    Object object = ois.readObject();
                    receiveBuffer.put(object);

                    if (object instanceof User) {
                        user = (User) object;
                        userName = user.getUsername();
                        userType = user.getUserType();

                        switch (userType) {
                            case LOGIN:
                                socketHashMap.put(userName, socketStreamObject);
                                break;
                            case LOGOUT:
                                running = false;
                                break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
