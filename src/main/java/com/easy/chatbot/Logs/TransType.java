package com.example.demo.Logs;

public enum TransType {
    PAY(-1), RECEIVE(1);

    private final int type;
    TransType(int type){
        this.type = type;
    }

    public int getTypeMultiplier(){
        return this.type;
    }
}
