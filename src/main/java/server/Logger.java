package server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Requirement: F.S.6.1
 */
public class Logger implements PropertyChangeListener {
    private ServerController controller;
    private LinkedList<LogEvent> eventLog;

    public Logger(ServerController controller){
        this.controller = controller;
        this.eventLog = new LinkedList<>();
        controller.addListener(this);
        loadLogsFromFile();
        logServerStart();
    }

    private void loadLogsFromFile() {
        try {
            FileInputStream fis = new FileInputStream("files/log.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            LinkedList<LogEvent> events = (LinkedList<LogEvent>) ois.readObject();
            this.eventLog = events;

        } catch (IOException | ClassNotFoundException e) {
        }
        try {
            FileOutputStream fos = new FileOutputStream("files/log.dat");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(eventLog);
            oos.flush();
            oos.close();
        } catch (IOException e) {
        }
    }

    public LinkedList<LogEvent> searchLogs (LocalDateTime startTime, LocalDateTime endTime) {
        LinkedList<LogEvent> events = new LinkedList<>();

        if (endTime == null)
            endTime = LocalDateTime.now();

        if (startTime == null)
            startTime = LocalDateTime.of(1995,11,1,13,37);

        for (LogEvent event : eventLog) {
            LocalDateTime currTime = event.getTime();

            if (currTime.isAfter(startTime) && currTime.isBefore(endTime)) {
                events.addFirst(event);
            }
        }

        return events;
    }

    public synchronized void writeToFile(){
        try {
            FileOutputStream fos = new FileOutputStream("files/log.dat", false);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(eventLog);
            oos.flush();
            oos.close();
        } catch (IOException e) {
        }

    }

    public void logServerStart() {
        LogEvent currEvent = new LogEvent();
        currEvent.setEvent("Server started");
        eventLog.add(currEvent);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        String s = null;
        LogEvent currEvent = new LogEvent();

        if(evt.getPropertyName().equals("Sending activity: ")) {
            s = String.format("Sending activity: %s to user %s", evt.getOldValue(), evt.getNewValue());
        }
        if(evt.getPropertyName().equals("New login: ")) {
            s = String.format("New Login: %s", evt.getNewValue());
        }
        if(evt.getPropertyName().equals("User logged out: ")) {
            s = String.format("User logged out: %s", evt.getNewValue());
        }
        if(evt.getPropertyName().equals("User interval: ")) {
            s = String.format("User %s changed interval to %s", evt.getOldValue(), evt.getNewValue());
        }
        if(evt.getPropertyName().equals("Activity completed: ")) {
            s = String.format("User %s completed %s", evt.getOldValue(), evt.getNewValue());
        }
        if(evt.getPropertyName().equals("Activity delayed: ")) {
            s = String.format("User %s delayed %s", evt.getOldValue(), evt.getNewValue());
        }
        if(evt.getPropertyName().equals("User wants activity: ")) {
            s = String.format("User %s asked for activity", evt.getNewValue());
        }

        if (s != null) {
            currEvent.setEvent(s);
            eventLog.add(currEvent);
            writeToFile();
        }

    }
}

