package com.example.demo.Entitys;

public class Client {
    private final int ID = (int) (Math.random()*99999);
    private String name;
    private String whatsapp;
    private String apiKey;

    public Client(){

    }

    public int getID() {
        return ID;
    }
}
