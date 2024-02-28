package server;

import shared.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Requirement: F.S.1
 * This class handles all the user objects.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class UserRegister {
    private Map<String, User> userHashMap;
    private ArrayList<User> userArrayList;

    public UserRegister() {
        userHashMap = new HashMap<>();
        userArrayList = new ArrayList<>();
    }

    public Map<String, User> getUserHashMap() {
        return userHashMap;
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    /**
     * Updates the HashMap and LinkedList with a new updated User object.
     *
     * @param updatedUser
     */
    public synchronized void updateUser(User updatedUser) {
        userHashMap.remove(updatedUser.getUsername());
        userHashMap.put(updatedUser.getUsername(), updatedUser);

        Iterator<User> iterator = userArrayList.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(updatedUser.getUsername())) {
                iterator.remove();
            }
        }
        userArrayList.add(updatedUser);
    }

}