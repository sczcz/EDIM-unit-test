package shared;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Requirement: F.O.1.2
 * This class creates a register that handles Activity objects.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class ActivityRegister {
    private LinkedList<Activity> activityRegister;

    public ActivityRegister(String file) {
        createRegister(file);
    }

    /**
     * Requirement: F.Ö.2, F.Ö.2.1
     * @param file
     */
    private void createRegister(String file) {
        activityRegister = new LinkedList<Activity>();
        int nbrOfActivities;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            nbrOfActivities = Integer.parseInt(br.readLine());
            for (int i = 0; i < nbrOfActivities; i++) {
                Activity activity = new Activity();
                activity.setActivityName(br.readLine());
                activity.setActivityInstruction(br.readLine());
                activity.setActivityInfo(br.readLine());
                activity.createActivityImage(br.readLine());
                activityRegister.add(activity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized LinkedList<Activity> getActivityRegister() {
        return activityRegister;
    }
}
