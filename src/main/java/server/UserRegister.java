package server;

import shared.User;

import java.util.*;

/**
 * Requirement: F.S.1
 * This class handles all the user objects.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class UserRegister {
    private Map<String, User> userHashMap;

    private LinkedList<User> userLinkedList;

    public UserRegister() {
        userHashMap = new HashMap<>();
        userLinkedList = new LinkedList<>();
    }

    public Map<String, User> getUserHashMap() {
        return userHashMap;
    }

    public void setUserHashMap(HashMap userList) {
        this.userHashMap = userList;
    }

    public LinkedList<User> getUserLinkedList() {
        return userLinkedList;
    }

    public void setUserLinkedList(LinkedList<User> userLinkedList) {
        this.userLinkedList = userLinkedList;
    }

    /**
     * Updates the HashMap and LinkedList with a new updated User object.
     *
     * @param updatedUser
     */
    public void updateUser(User updatedUser) {
        userHashMap.remove(updatedUser.getUsername());
        userHashMap.put(updatedUser.getUsername(), updatedUser);

        Iterator<User> iterator = userLinkedList.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(updatedUser.getUsername())) {
                iterator.remove();
            }
        }
        userLinkedList.add(updatedUser);
    }

    public ArrayList<User> getUsersOnline(ArrayList<String> userNames) {
        ArrayList<User> usersOnline = new ArrayList<>();
        for (String userName : userNames) {
                usersOnline.add(userHashMap.get(userName));

        }
        return usersOnline;
    }
}