package server;

import shared.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class handles the logic of the in and out coming objects from the clients.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ServerController extends Thread {
    private HashMap<String, SocketStreamObject> socketHashMap;
    private ReceiverServer receiverServer;
    private SenderServer senderServer;
    private UserRegister userRegister;
    private ActivityRegister activityRegister;
    private Random rand;
    private String className = "Class: ServerController ";
    private Buffer<Object> receiveBuffer;
    private Buffer<Object> sendBuffer;
    private String userFilePath = "users.dat";
    private Logger log;
    private PropertyChangeSupport changeSupport;
    private LoggerGUI loggerGUI;
    private ArrayList<String> usersOnline = new ArrayList<>();

    /**
     * Constructs all the buffers and servers and HashMaps that is needed.
     *
     * @param port the received port number.
     */
    public ServerController(int port) {
        receiveBuffer = new Buffer<>();
        sendBuffer = new Buffer();
        socketHashMap = new HashMap();
        receiverServer = new ReceiverServer(port, socketHashMap, receiveBuffer);
        senderServer = new SenderServer(socketHashMap, sendBuffer);
        userRegister = new UserRegister();
        readUsers(userFilePath);
        activityRegister = new ActivityRegister("activities.txt");
        rand = new Random();
        this.changeSupport = new PropertyChangeSupport(this);
        log = new Logger(this);
        loggerGUI = new LoggerGUI(this);
        callSearchLogger(null, null);
    }

    public void addListener(PropertyChangeListener pcl) {
        changeSupport.addPropertyChangeListener(pcl);
    }

    public void callSearchLogger(LocalDateTime startTime, LocalDateTime endTime) {
        LinkedList<LogEvent> logs = log.searchLogs(startTime, endTime);
        ArrayList<String> formattedStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (LogEvent log : logs) {
            formattedStrings.add(String.format("[%s] %s", log.getTime().format(formatter), log.getEvent()));
        }
        loggerGUI.listLog(formattedStrings.toArray());
    }

    /**
     * Opens a stream and writes the user objects to the stream and then creates a file.
     *
     * @param filename the name of the created file.
     */
    public void writeUsers(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).getFile())))) {

            oos.writeInt(userRegister.getUserLinkedList().size());
            for (User user : userRegister.getUserLinkedList()) {
                oos.writeObject(user);
            }
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a text file from the files folder and adds them to the contacts array. Then sets the contacts array to oldContactList array.
     *
     * @param filename the read filename.
     */
    public void readUsers(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(filename)))) {

            if (ois != null) {
                int size = ois.readInt();
                for (int i = 0; i < size; i++) {
                    try {
                        User user = (User) ois.readObject();
                        userRegister.getUserHashMap().put(user.getUsername(), user);
                        userRegister.getUserLinkedList().add(user);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requirement: F.S.1
     * Checks whether the User who recently logged in is a new user or not.
     *
     * @param user the logged in User.
     * @return an updated User.
     */
    public User checkLoginUser(User user) {
        if (!userRegister.getUserHashMap().isEmpty()) {

            if (userRegister.getUserHashMap().containsKey(user.getUsername())) { //userRegister.getUserHashMap().get(user.getUserName()).getUserName().equals(user.getUserName())
                user = userRegister.getUserHashMap().get(user.getUsername());
                user.setUserType(UserType.SENDUSER);
            } else {
                user.setUserType(UserType.SENDWELCOME);
                userRegister.getUserHashMap().put(user.getUsername(), user);
                userRegister.getUserLinkedList().add(user);
                writeUsers(userFilePath);
            }
        } else {
            user.setUserType(UserType.SENDWELCOME);
            userRegister.getUserHashMap().put(user.getUsername(), user);
            userRegister.getUserLinkedList().add(user);
            writeUsers(userFilePath);
        }
        return user;
    }

    /**
     * Requirement: F.A.1, F.A.2, F.A.3
     * Receives a username and gets a User-object from the HashMap. If the user's activity has been delayed a delayed activity is sent to the sendBuffer,
     * else a new activity is sent to the client.
     *
     * @param username the received username.
     */
    public void sendActivity(String username) {
        User user = userRegister.getUserHashMap().get(username);

        System.out.println("username in sendActivity(): " + user.getUsername());

        if (user.getDelayedActivity() != null) {
            sendBuffer.put(user.getDelayedActivity());
            user.setDelayedActivity(null);
        } else {
            activityRegister = new ActivityRegister("activities.txt");
            int nbrOfActivities = activityRegister.getActivityRegister().size();
            System.out.println("nbrOfActivities: " + nbrOfActivities);
            int activityNbr = 1;
            Activity activityToSend = new Activity();

            Activity getActivity = activityRegister.getActivityRegister().get(activityNbr);
            activityToSend.setActivityName(getActivity.getActivityName());
            activityToSend.setActivityInstruction(getActivity.getActivityInstruction());
            activityToSend.setActivityInfo(getActivity.getActivityInfo());
            activityToSend.setActivityUser(username);
            activityToSend.setActivityImage(getActivity.getActivityImage());

            System.out.println("Activity to send: " + activityToSend.getActivityName());

            sendBuffer.put(activityToSend);
            System.out.println("Sending activity: " + activityToSend.getActivityName());
            changeSupport.firePropertyChange("Sending activity: ", activityToSend.getActivityName(), username);
        }
    }

    /**
     * Receives the socket with the username and closes it and removes the socket from the HashMap.
     *
     * @param username
     */
    public void logOutUser(String username) {
        try {
            sleep(5000);
            socketHashMap.get(username).getOos().close();
            socketHashMap.get(username).getOis().close();
            socketHashMap.get(username).getSocket().close();
            socketHashMap.remove(username);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("User logged out: " + username);
    }

    /**
     * Requirements: F.A.2, F.A.3, F.K.1, F.S.2
     * Receives a User object from the receive-Buffer and checks if it's a User or a Activity.
     */
    public void run() {
        while (true) {
            try {
                Object object = receiveBuffer.get();

                if (object instanceof User) {
                    User user = (User) object;
                    String userName = user.getUsername();
                    UserType userType = user.getUserType();

                    switch (userType) {
                        case LOGIN:
                            User updatedUser = checkLoginUser(user);
                            changeSupport.firePropertyChange("New login: ", null, userName);
                            sendBuffer.put(updatedUser);
                            if (!usersOnline.contains(updatedUser.getUsername())) {
                                usersOnline.add(updatedUser.getUsername());
                            }
                            sendBuffer.put(userRegister.getUsersOnline(usersOnline));
                            break;
                        case LOGOUT:
                            sendBuffer.put(user);
                            logOutUser(userName);
                            changeSupport.firePropertyChange("User logged out: ", null, userName);
                            writeUsers(userFilePath);
                            usersOnline.remove(user.getUsername());
                            if (!usersOnline.isEmpty()) {
                                sendBuffer.put(userRegister.getUsersOnline(usersOnline));
                            }
                            break;
                        case SENDINTERVAL:
                            userRegister.updateUser(user);
                            changeSupport.firePropertyChange("User interval: ", userName, user.getNotificationInterval());
                            break;
                        case WANTACTIVITY:
                            sendActivity(userName);
                            changeSupport.firePropertyChange("User wants activity: ", null, userName);
                            break;
                    }
                } else if (object instanceof Activity) {
                    Activity activity = (Activity) object;
                    String username = activity.getActivityUser();

                    if (activity.isCompleted()) {
                        changeSupport.firePropertyChange("Activity completed: ", username, activity.getActivityName());
                    } else {
                        changeSupport.firePropertyChange("Activity delayed: ", username, activity.getActivityName());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
