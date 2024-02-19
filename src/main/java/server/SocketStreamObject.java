package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Requirement: K.G.2
 * This class handles a socket and the input/output streams.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class SocketStreamObject {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public SocketStreamObject(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket=socket;
        this.ois = ois;
        this.oos = oos;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

}
