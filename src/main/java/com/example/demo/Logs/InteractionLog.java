package com.example.demo.Logs;

import com.example.demo.Entitys.Client;

import java.util.Date;

public class InteractionLog {
    private final int interactionID = (int) (Math.random()*99999);
    private int clientID;
    private String[] messageLogs;
    private Date interStart;
    private Date interEnd;
    InteractionLog(Client client, Date initial, Date end) {
        this.clientID = client.getID();
        this.interStart = initial;
        this.interEnd = end;
    }

    public int getInteractionID() {
        return interactionID;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String[] getMessageLogs() {
        return messageLogs;
    }

    public void setMessageLogs(String[] messageLogs) {
        this.messageLogs = messageLogs;
    }

    public Date getInterStart() {
        return interStart;
    }

    public void setInterStart(Date interStart) {
        this.interStart = interStart;
    }

    public Date getInterEnd() {
        return interEnd;
    }

    public void setInterEnd(Date interEnd) {
        this.interEnd = interEnd;
    }
}
