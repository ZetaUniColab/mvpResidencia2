package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String whatsapp;

    private String apiKey;

    public Client() {
    }

    public Client(String name, String whatsapp, String apiKey) {
        this.name = name;
        this.whatsapp = whatsapp;
        this.apiKey = apiKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
