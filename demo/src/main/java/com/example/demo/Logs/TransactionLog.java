package com.example.demo.Logs;

import com.example.demo.Entitys.Client;

import java.util.Date;

public class TransactionLog {
    private int accountID;
    private int clientID;
    private TransType type;
    private String description;
    private double value;
    private Date expectedDate;
    private String status;
    TransactionLog(Client account, Client client, TransType type, String description, double value, Date expectedDate, String status){
        this.accountID = account.getID();
        this.clientID = client.getID();
        this.type = type;
        this.description = description;
        this.value = value;
        this.expectedDate = expectedDate;
        this.status = status;
    }
}
