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

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public TransType getType() {
        return type;
    }

    public void setType(TransType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
