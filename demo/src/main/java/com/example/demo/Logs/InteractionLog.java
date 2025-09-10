package com.example.demo.Logs;

import com.example.demo.Entitys.Client;

import java.util.Date;

public class InteractionLog {
    private final int interactionID = (int) (Math.random()*99999);
    private int clientID;
    private String[] messageLogs;
    private Date interStart;
    private Date interEnd;
    InteractionLog(Client client){
        this.clientID = client.getID();
    }
}
