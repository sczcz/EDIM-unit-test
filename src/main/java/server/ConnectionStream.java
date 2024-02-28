package server;

import shared.User;
import shared.UserType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionStream extends Thread {
    private ServerController serverController;
    private volatile boolean running = true;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     * Creates a Socket Stream Object with the received socket.
     *
     * @param socket the received object.
     */
    public ConnectionStream(Socket socket, ServerController serverController) {
        this.serverController = serverController;
        this.socket = socket;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.start();
    }


    public boolean sendObject(Object object) {
        try {
            oos.writeUnshared(object);
            oos.flush();
            return true;}
        catch (IOException e) {
            return false;
        }
    }

    /**
     * Requirement: K.G.2
     * Reads an object from the input stream and checks its value and send it to {@link ServerController}.
     */
    public void run() {
        while (running) {
            try {
                Object object = ois.readObject();
                System.out.println("CS: mottagit ett objekt: " + object.getClass());  //TODO: Test - ta bort

                if (object instanceof User) {
                    User user = (User) object;
                    UserType userType = user.getUserType();
                    String userName = user.getUsername();

                    switch (userType) {
                        case LOGIN:
                            serverController.addNewConnection(userName, this);
                            System.out.println("CS: " + userName + " lades till i HashMap"); //TODO: Test - ta bort
                            break;
                        case LOGOUT:
                            sendObject(object);
                            oos.close();
                            ois.close();
                            socket.close();
                            serverController.logOutUser(userName);
                            running = false;
                            break;
                    }
                }
                serverController.receiveObject(object);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

