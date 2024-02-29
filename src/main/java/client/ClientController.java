package client;

import client.gui.MainFrame;
import shared.Activity;
import shared.ActivityRegister;
import shared.User;
import shared.UserType;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class manages the logic for the Client and controls the data flow.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ClientController {

    private MainFrame mainFrame;
    private ClientCommunicationController ccc;
    private User user;
    private ActivityRegister activityRegister;

    /**
     * Constructs a MainFrame and a ClientCommunicationController object. Then calls the method createUser.
     */
    public ClientController() {
        mainFrame = new MainFrame(this);
    }

    /**
     * Receives a String and creates a new User object and calls the logIn method.
     *
     * @param userName
     */
    public void createUser(String userName) {
        user = new User(userName);
        logIn();
    }

    /**
     * Receives an Activity Object and sends it forth to the ClientCommunicationController.
     *
     * @param activity the received object.
     */
    public void sendActivityToCCC(Activity activity) {
        user.addActivityToList(activity);
        ccc.sendObject(activity);
    }

    /**
     * Sets the UserType to LOGIN and sends the user object to ClientCommunicationController.
     */
    public void logIn() {
        user.setUserType(UserType.LOGIN);
        ccc = new ClientCommunicationController(this);
        if (user.getUserType() != UserType.OFFLINE) {
            ccc.sendObject(user);
        }
    }

    public boolean isOffline() {
        return user.getUserType() == UserType.OFFLINE;
    }

    /**
     * Sets the UserType to LOGOUT and sends the user object to ClientCommunicationController.
     */
    public void logOut() {
        user.setUserType(UserType.LOGOUT);
        if (user.getUserType() != UserType.OFFLINE) {
            ccc.sendObject(user);
        }
    }

    /**
     * Requirement: F.O.1
     * Sets the UserType to OFFLINE and instantiates a new ActivityRegister
     */
    public void runOffline() {
        user.setUserType(UserType.OFFLINE);
        activityRegister = new ActivityRegister("files/activities.txt");
    }


    /**
     * Requirement: F.A.1, F.A.2, F.O.1.2
     * Checks if the User has delayedActivity, if so, sends a new notification for it.
     * If not: Sets the UserType to WANTACTIVITY and sends the user object to ClientCommunicationController
     */
    public void requestActivity() {
        if (user.getUserType() == UserType.OFFLINE) {
            if (user.getDelayedActivity() == null) {
                mainFrame.showNotification(getOfflineActivity());
            } else {
                mainFrame.showNotification(user.getDelayedActivity());
            }
            return;
        }
        if (user.getDelayedActivity() == null) {
            user.setUserType(UserType.WANTACTIVITY);
            ccc.sendObject(user);
        } else {
            mainFrame.showNotification(user.getDelayedActivity());
        }
    }


    /**
     * Requirement: F.A.1
     *
     * @return
     */
    public Activity getOfflineActivity() {
        Random rand = new Random();
        int nbrOfActivities = activityRegister.getActivityRegister().size();
        int activityNbr = rand.nextInt(nbrOfActivities);
        Activity activityToSend = new Activity();
        Activity getActivity = activityRegister.getActivityRegister().get(activityNbr);
        activityToSend.setActivityName(getActivity.getActivityName());
        activityToSend.setActivityInstruction(getActivity.getActivityInstruction());
        activityToSend.setActivityInfo(getActivity.getActivityInfo());
        activityToSend.setActivityUser(user.getUsername());
        activityToSend.setActivityImage(getActivity.getActivityImage());
        return activityToSend;
    }

    /**
     * Receives an Activity object an sends it forth to MainFrame.
     *
     * @param activity the received object.
     */
    public void receiveNotificationFromCCC(Activity activity) {
        mainFrame.showNotification(activity);
    }

    /**
     * Requirement: F.P.1.1
     * Replaces the temporary user object with the already existing object from the server.
     * If it's a new user, a welcome message is sent.
     *
     * @param user the received object.
     */
    public synchronized void receiveUser(User user) {
        UserType userType = user.getUserType();
        this.user = user;
        if (userType == UserType.SENDWELCOME) {
            mainFrame.sendWelcomeMessage();
        }
    }

    public void receiveObject(Object object) {
        if (object instanceof User) {
            User user = (User) object;
            receiveUser(user);
            if (user.getUserType() == UserType.LOGOUT) {
                System.out.println("Vi kom in i receiveObject och ville disconnecta från server");
                ccc.disconnect();
            }
        } else if (object instanceof Activity) {
            Activity activity = (Activity) object;
            receiveNotificationFromCCC(activity);
        } else if (object instanceof ArrayList<?>) {
            ArrayList<String> usersOnline = (ArrayList<String>) object;
            updateOnlineList(usersOnline);
        }
    }

    /**
     * Sets a users interval and the UserType to SENDINTERVAL and sends it to {@link ClientCommunicationController}.
     *
     * @param interval the notification interval.
     */
    public void setInterval(int interval) {
        if (user.getUserType() != UserType.OFFLINE) {
            user.setNotificationInterval(interval);
            user.setUserType(UserType.SENDINTERVAL);
            ccc.sendObject(user);
        }
    }

    /**
     * Requirements: F.K.1
     *
     * @param usersOnline
     */
    public void updateOnlineList(ArrayList<String> usersOnline) {
       ArrayList<String> usersOnlineString = new ArrayList<>();
        for (String user : usersOnline) {
            if (!this.user.getUsername().equals(user)) {
                usersOnlineString.add(user);
            }
        }
        mainFrame.showUsersOnline(usersOnlineString);
    }

    public User getUser() {
        return this.user;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

}
