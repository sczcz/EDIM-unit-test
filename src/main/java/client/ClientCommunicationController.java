package client;

import shared.User;
import shared.UserType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Requirement: K.G.2
 * This class manages the communication between the Client classes and the Server classes.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ClientCommunicationController {
    private ClientController clientController;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private volatile boolean isConnected = true;
    private volatile boolean oisIsNull = true;

    /**
     * Receives a clientController object and then try to connect with the server.
     * ClientSender and a ClientReceiver Object. Then starts two Threads.
     *
     * @param clientController The received ClientController object.
     */
    public ClientCommunicationController(ClientController clientController) {
        this.clientController = clientController;
        if (connect()) {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("CCC - CS: oos igång");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new ClientReceiver().start();
        } else {
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
        System.out.println("Disconnecting");
        try {
            Thread.sleep(1000);
            oos.close();
            socket.close();
            System.out.println("CCC: Socket stängd. User: " + clientController.getUser().getUsername());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method ....
     *
     * @param object the object to be sent.
     */

    public boolean sendObject(Object object) {
        try {
            if (oos != null) {
                oos.writeUnshared(object);
                oos.flush();
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
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
                    System.out.println("CCC - CR: ois igång");
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
                    if(object instanceof User) {
                        System.out.println("Det var en user");
                        if (((User) object).getUserType() == UserType.LOGOUT ) {
                            System.out.println("den var logout");
                            ois.close();
                            isConnected = false;
                        }
                    }
                    System.out.println("CCC - CR: Mottaget: " + object.getClass()); //TODO: Test - ta bort
                    clientController.receiveObject(object);
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //TODO: Test - ta bort
        }

    }
}
