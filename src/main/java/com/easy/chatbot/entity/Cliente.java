package com.easy.chatbot.entity;


import jakarta.persistence.*;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id_cliente;

    @Column
    private String nome;

    @Column
    private String whatsapp;

    @Column(unique = true)
    private String chave_api;




}
