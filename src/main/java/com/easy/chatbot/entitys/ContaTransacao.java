package com.easy.chatbot.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/* Entidade JPA responsável pelo mapeamento da tabela de transações financeiras.*/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaTransacao {

    /**
     * Identificador único dPrimary Key
     * Gerado automaticamente pelo banco de dados via estratégia de auto-incremento (Identity).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_conta;

    /**
     * Mapeamento do relacionamento com a entidade Cliente.
     * Define a chave estrangeira (Foreign Key) 'id_cliente' na tabela 'conta_transacao'.
     */
    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    /**
     * Define a natureza da operação financeira.
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Descritivo detalhado da transação.
     * Utilizado para exibição nos relatórios e mensagens do chatbot
     */
    @Column(nullable = false)
    private String descricao;

    /**
     * Valor monetário da transação.
     * Armazenado como double para simplificação
     */
    @Column(nullable = false)
    private double valor;

    /**
     * Data de competência ou vencimento previsto para a transação.
     * Essencial para a lógica de filtragem de relatórios por período
     */
    @Column(nullable = false)
    private LocalDate data_previsao;

    /**
     * Estado atual da transação.
     */
    @Column(nullable = false)
    private String status;
}