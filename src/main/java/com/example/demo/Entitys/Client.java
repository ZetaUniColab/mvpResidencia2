package com.example.demo.Entitys;

public class Client {
    private final int ID = (int) (Math.random()*99999);
    private String name;
    private String whatsapp;
    private String apiKey;

    public Client(String Name, String Whatsapp, String ApiKey) {
    this.name = Name;
    this.whatsapp = Whatsapp;
    this.apiKey = ApiKey;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
