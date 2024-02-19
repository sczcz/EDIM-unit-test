package server;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Requirement: F.S.6.1
 */
public class LogEvent implements Serializable {

    private LocalDateTime time;
    private String event;

    public LogEvent() {
        this.time = LocalDateTime.now();
    }

    public LocalDateTime getTime() {
        return time;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
}
