package com.easy.chatbot.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidade JPA responsável pelo mapeamento da tabela de transações financeiras.
 * Representa a unidade atômica de movimentação (contas a pagar ou receber) no sistema,
 * mantendo o vínculo relacional com o cliente proprietário do registro.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContaTransacao {

    /**
     * Identificador único da transação (Primary Key).
     * Gerado automaticamente pelo banco de dados via estratégia de auto-incremento (Identity).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_conta;

    /**
     * Mapeamento do relacionamento com a entidade Cliente.
     * Define a chave estrangeira (Foreign Key) 'id_cliente' na tabela 'conta_transacao'.
     *
     * Nota Arquitetural: A cardinalidade aqui define como as tabelas se relacionam.
     * Em um cenário real de N transações para 1 Cliente, o ideal seria @ManyToOne.
     * Mantido @OneToOne conforme especificação atual do código legado/MVP.
     */
    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    /**
     * Define a natureza da operação financeira.
     * Exemplos esperados: 'PAGAR' ou 'RECEBER'.
     * A restrição nullable = false garante integridade de dados, impedindo registros órfãos de tipo.
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Descritivo detalhado da transação.
     * Utilizado para exibição nos relatórios e mensagens do chatbot (ex: "Boleto Energia").
     */
    @Column(nullable = false)
    private String descricao;

    /**
     * Valor monetário da transação.
     * Armazenado como double para simplificação, embora BigDecimal seja a prática recomendada
     * para sistemas financeiros de alta precisão para evitar erros de ponto flutuante.
     */
    @Column(nullable = false)
    private double valor;

    /**
     * Data de competência ou vencimento previsto para a transação.
     * Essencial para a lógica de filtragem de relatórios por período (15/30 dias).
     */
    @Column(nullable = false)
    private LocalDate data_previsao;

    /**
     * Estado atual da transação no ciclo de vida financeiro.
     * Exemplos: 'PENDENTE', 'PAGO', 'CANCELADO'.
     */
    @Column(nullable = false)
    private String status;
}