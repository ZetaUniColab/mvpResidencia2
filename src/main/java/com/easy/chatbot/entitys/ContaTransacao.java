package com.easy.chatbot.entitys;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ContaTransacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_conta;

    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDate data_previsao;

    @Column(nullable = false)
    private String status;
}
