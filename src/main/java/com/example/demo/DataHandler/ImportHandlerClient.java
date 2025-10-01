package com.example.demo.DataHandler;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Client")
@Setter
@Getter


public class ImportHandlerClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_Client;

    @Column(nullable = false)
    private String Name;

    @Column(nullable = false)
    private String Whatsapp;

    @Column(nullable = false)
    private String API_Key;
}
