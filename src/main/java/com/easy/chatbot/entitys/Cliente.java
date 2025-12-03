package com.easy.chatbot.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa a tabela 'cliente'.
 * Armazena dados cadastrais e informações de controle de sessão/segurança.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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

    // --- Controle de Acesso ---

    @Column(length = 11)
    private String cpf;

    /* Armazena o timestamp de expiração do token de sessão*/
    @Column(name = "data_validade_token")
    private LocalDateTime dataValidadeToken;

    public Cliente(String nome, String whatsapp) {
        this.nome = nome;
        this.whatsapp = whatsapp;
    }
}