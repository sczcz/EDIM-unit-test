package server;

import shared.Activity;
import shared.Buffer;
import shared.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class creates a thread pool and handles the communication to the Client.
 *
 * @version 1.0
 * @author Sofia Hallberg, Chanon Borgstrom.
 */

public class SenderServer {
    private ObjectOutputStream oos;
    private String className = "Class: SenderServer ";
    private LinkedList<WorkerThread> threadPool;
    private Map<String, SocketStreamObject> socketHashMap;
    private Buffer sendBuffer;


    /**
     * Receives all necessary data and then generates and starts the thread pool.
     *
     * @param socketHashMap
     * @param sendBuffer
     */
    public SenderServer(HashMap<String, SocketStreamObject> socketHashMap, Buffer sendBuffer) {
        this.socketHashMap = socketHashMap;
        this.sendBuffer = sendBuffer;
        this.threadPool = new LinkedList<>();
        generateThreadPool(1);
        startThreadPool();
    }

    /**
     * Generates a thread pool with the received int.
     *
     * @param nbrOfConnections received number of connections.
     */
    public void generateThreadPool(int nbrOfConnections) {
        for (int i = 0; i < nbrOfConnections; i++) {
            threadPool.add(new WorkerThread());
        }
    }

    public int getThreadPoolSize() {
        return threadPool.size();
    }

    /**
     * Starts the thread pool.
     */
    public void startThreadPool() {
        for (WorkerThread thread : threadPool) {
            thread.start();
        }
    }

    // Inner thread class: handles outgoing communication.
    private class WorkerThread extends Thread {

        /**
         * Requirement: K.G.2
         * Receives an object from the buffer and sends it to the Client.
         */
        public void run() {
            while (true) {
                try {
                    Object object = sendBuffer.get();
                    if (object instanceof User) {
                        User sendUser = (User) object;
                        oos = socketHashMap.get(sendUser.getUsername()).getOos();
                        oos.writeObject(sendUser);
                        oos.flush();
                    } else if (object instanceof Activity) {
                        Activity sendNewActivity = (Activity) object;
                        oos = socketHashMap.get(sendNewActivity.getActivityUser()).getOos();
                        oos.writeObject(sendNewActivity);
                        oos.flush();
                    } else if (object instanceof ArrayList<?>){
                        ArrayList<User> usersOnline = (ArrayList<User>) object;
                        if(!socketHashMap.isEmpty()){
                            for(User user : usersOnline){
                                oos = socketHashMap.get(user.getUsername()).getOos();
                                oos.writeObject(usersOnline);
                                oos.flush();
                            }
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
