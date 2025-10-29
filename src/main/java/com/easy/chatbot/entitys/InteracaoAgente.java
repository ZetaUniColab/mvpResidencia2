package com.easy.chatbot.entitys;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class InteracaoAgente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_interacao;

    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column
    private String registro_interacao;

    @Column
    private LocalDate data_inicio;

    @Column
    private LocalDate data_termino;


}
