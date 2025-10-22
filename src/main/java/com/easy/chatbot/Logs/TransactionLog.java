package com.example.demo.Logs;

import com.example.demo.Logs.TransType;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Getter
@Entity
@Table(name = "transaction_logs")
public class TransactionLog {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    private int accountID;
    private int clientID;

    @Enumerated(EnumType.STRING)
    private TransType type;

    @Column(length = 1000)
    private String description;

    private double value;

    @Temporal(TemporalType.DATE)
    private Date expectedDate;

    private String status;

    public TransactionLog() {
    }

    public TransactionLog(int accountID, int clientID, TransType type, String description, double value, Date expectedDate, String status) {
        this.accountID = accountID;
        this.clientID = clientID;
        this.type = type;
        this.description = description;
        this.value = value;
        this.expectedDate = expectedDate;
        this.status = status;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setType(TransType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
