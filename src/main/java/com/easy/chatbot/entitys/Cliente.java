package com.easy.chatbot.entitys;


import jakarta.persistence.*;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_cliente;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String whatsapp;

    @Column(unique = true)
    private String chave_api;

    public Cliente(long id_cliente, String nome, String whatsapp, String chave_api) {
        this.id_cliente = id_cliente;
        this.nome = nome;
        this.whatsapp = whatsapp;
        this.chave_api = chave_api;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getChave_api() {
        return chave_api;
    }

    public void setChave_api(String chave_api) {
        this.chave_api = chave_api;
    }
}
