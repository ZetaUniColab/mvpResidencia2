package com.easy.chatbot.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidade JPA responsável pelo registro de auditoria e histórico de interações.
 * Esta classe mapeia a tabela que armazena os logs de conversas ou sessões realizadas
 * entre o Cliente e o Agente Virtual (Bot), permitindo análises futuras de atendimento.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InteracaoAgente {

    /**
     * Identificador único da sessão de interação (Primary Key).
     * Gerado automaticamente pela estratégia de identidade do banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_interacao;

    /**
     * Referência ao Cliente que iniciou a interação.
     * Mapeia a chave estrangeira 'id_cliente'.
     *
     * Nota Arquitetural: A anotação @OneToOne indica um relacionamento 1:1.
     * Em um cenário de produção onde um cliente possui múltiplos históricos,
     * a modelagem ideal seria @ManyToOne. Mantido conforme especificação do MVP.
     */
    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    /**
     * Campo descritivo para armazenar o resumo, log bruto ou metadados da conversa.
     * Pode conter JSON, texto livre ou códigos de status da interação.
     */
    @Column
    private String registro_interacao;

    /**
     * Timestamp do início da sessão de atendimento.
     * Fundamental para cálculo de métricas de tempo de resposta e duração de atendimento.
     */
    @Column
    private LocalDate data_inicio;

    /**
     * Timestamp de finalização da sessão.
     * Utilizado em conjunto com data_inicio para determinar o SLA do atendimento.
     */
    @Column
    private LocalDate data_termino;
}