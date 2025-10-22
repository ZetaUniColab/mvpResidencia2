package com.example.demo.Logs;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Date;

@Getter
@Entity
@Table(name = "interaction_logs")
public class InteractionLog {

    // Getters and setters
    @Id
    private int interactionID;

    private int clientID;

    @Column(length = 5000)
    private String messageLogs; // Store as comma-separated string or JSON

    @Temporal(TemporalType.TIMESTAMP)
    private Date interStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date interEnd;

    public InteractionLog() {
        this.interactionID = (int) (Math.random() * 99999);
    }

    public InteractionLog(int clientID, Date interStart, Date interEnd, String[] logs) {
        this();
        this.clientID = clientID;
        this.interStart = interStart;
        this.interEnd = interEnd;
        this.setMessageLogsFromArray(logs);
    }

    // Convert String[] to String
    public void setMessageLogsFromArray(String[] messages) {
        this.messageLogs = String.join(" | ", messages);
    }

    // Convert back to array if needed
    public String[] getMessageLogsAsArray() {
        return messageLogs != null ? messageLogs.split(" \\| ") : new String[0];
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setMessageLogs(String messageLogs) {
        this.messageLogs = messageLogs;
    }

    public void setInterStart(Date interStart) {
        this.interStart = interStart;
    }

    public void setInterEnd(Date interEnd) {
        this.interEnd = interEnd;
    }

    @Override
    public String toString() {
        return "InteractionLog{" +
                "interactionID=" + interactionID +
                ", clientID=" + clientID +
                ", messageLogs=" + Arrays.toString(getMessageLogsAsArray()) +
                ", interStart=" + interStart +
                ", interEnd=" + interEnd +
                '}';
    }
}
