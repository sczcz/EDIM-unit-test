package shared;

import javax.swing.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


/**
 * This class handles the information about an Activity object.
 *
 * @author Carolin Nordstrom, Oscar Kareld, Chanon Borgstrom, Sofia Hallberg.
 * @version 1.0
 */

public class Activity implements Serializable {
    private static final long serialVersionUID = 200428L;
    private String activityName;
    private String activityInstruction;
    private String activityInfo;
    private boolean isCompleted = false;
    private String activityUser;
    private ImageIcon activityImage;

    public Activity() {
    }

    public Activity(String activityName) {
        this.activityName = activityName;
    }

    //BELOW IS FOR TESTING PURPOSES ONLY
    public Activity(String activityName, String activityInstruction, String activityInfo, String activityUser, ImageIcon activityImage) {
        this.activityName = activityName;
        this.activityInstruction = activityInstruction;
        this.activityInfo = activityInfo;
        this.activityUser = activityUser;
        this.activityImage = activityImage;
    }
    //ABOVE IS FOR TESTING PURPOSES ONLY

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        String hours;
        String minutes;
        if (cal.getTime().getHours() < 10){
            hours = "0" + cal.getTime().getHours();
        } else {
            hours = "" + cal.getTime().getHours();
        }
        if (cal.getTime().getMinutes() < 10){
            minutes = "0" + cal.getTime().getMinutes();
        } else {
            minutes = "" + cal.getTime().getMinutes();
        }
        String time = hours + ":" + minutes;

        return time;
    }


    public String getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(String activityInfo) {
        this.activityInfo = activityInfo;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }

    /**
     * Requirement: F.Ö.1
     * @return
     */
    public String getActivityInstruction() {
        return activityInstruction;
    }

    /**
     * Requirement: F.Ö.1
     * @param activityInstruction
     */
    public void setActivityInstruction(String activityInstruction) {
        this.activityInstruction = activityInstruction;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getActivityUser() {
        return activityUser;
    }

    public void setActivityUser(String activityUser) {
        this.activityUser = activityUser;
    }

    public void setActivityImage(ImageIcon icon) {
        activityImage = icon;
    }

    public ImageIcon getActivityImage() {
        return activityImage;
    }

    /**
     * Requirements: F.A.7
     * @param fileName
     */
    public void createActivityImage(String fileName) {
        activityImage = new ImageIcon(fileName);
    }

    //BELOW IS FOR TESTING PURPOSES ONLY

    //ABOVE IS FOR TESTING PURPOSES ONLY
}


