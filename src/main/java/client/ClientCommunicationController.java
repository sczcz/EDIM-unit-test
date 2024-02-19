package client;

import shared.Activity;
import shared.User;
import shared.UserType;
import shared.Buffer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Requirement: K.G.2
 * This class manages the communication between the Client classes and the Server classes.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ClientCommunicationController {
    private ClientController clientController;
    private Buffer buffer;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private String className = "Class: ClientCommunicationController ";
    private volatile boolean isConnected = true;
    private volatile boolean oisIsNull = true;

    /**
     * Receives a clientController object and then try to connect with the server. Constructs a buffer,
     * ClientSender and a ClientReceiver Object. Then starts two Threads.
     *
     * @param clientController The received ClientController object.
     */
    public ClientCommunicationController(ClientController clientController) {
        this.clientController = clientController;
        if (connect()) {buffer = new Buffer();
            new ClientSender().start();
            new ClientReceiver().start();}
        else {
            buffer = new Buffer();
            clientController.runOffline();
        }

    }

    /**
     * Tries to create a new socket and connect to the server's IP.
     */
    public boolean connect() {
        try {
            socket = new Socket("127.0.0.1", 4343);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * This method tries to close the socket and the connection to the server.
     */
    public void disconnect() {
        isConnected = false;
        try {
            Thread.sleep(2000);
            socket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method lays an object in a buffer which mission is to be sent to the server.
     *
     * @param object the object to be sent.
     */

    public void sendObject(Object object) {
        buffer.put(object);
    }

    // ClientSender starts a new thread which retrieves an object from a buffer and sends it to the server.
    private class ClientSender extends Thread {

        /**
         * Tries to construct a OutPutStream.
         */
        public ClientSender() {
            try {
                if (oos == null) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * A thread which retrieves an object from the buffer and then writes it to the stream.
         */
        public void run() {
            while (isConnected) {
                try {
                    Object object = buffer.get();
                    oos.writeUnshared(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientReceiver extends Thread {
        private Object object;

        /**
         * Tries to open an Input Stream then tries to read an object from the stream.
         * Then checks the object's class value and sends it to {@link ClientController}.
         */

        //TODO: Bug related to issue [EDIM-40] is likely solved here or disconnect()-method.
        public void run() {
            while (oisIsNull) {
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                    oisIsNull = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            while (isConnected) {
                try {
                    sleep(500);
                    object = ois.readObject();
                    if (object instanceof User) {
                        User user = (User) object;
                        clientController.receiveUser(user);
                        if (user.getUserType() == UserType.LOGOUT) {
                            disconnect();
                        }
                    } else if (object instanceof Activity) {
                        Activity activity = (Activity) object;
                        clientController.receiveNotificationFromCCC(activity);
                    } else if (object instanceof ArrayList<?>) {
                        ArrayList<User> usersOnline = (ArrayList<User>) object;
                        clientController.receiveOnlineList(usersOnline);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }
}
