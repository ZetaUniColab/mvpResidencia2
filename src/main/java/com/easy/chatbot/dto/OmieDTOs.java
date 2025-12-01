package com.easy.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Agrupador de DTOs (Data Transfer Objects) para a integração com a API da Omie.
 * Utiliza classes estáticas internas para organizar as estruturas de Request e Response,
 * garantindo o mapeamento correto entre o JSON da API externa e os objetos Java.
 */
public class OmieDTOs {

    /**
     * Estrutura padrão para envio de requisições POST à API da Omie.
     * A Omie utiliza um padrão RPC onde o método é definido no corpo do JSON (parâmetro 'call').
     */
    @Data
    public static class OmieRequest {
        public String call;

        // @JsonProperty mapeia o snake_case do JSON para o camelCase do Java
        @JsonProperty("app_key") public String appKey;
        @JsonProperty("app_secret") public String appSecret;

        // Lista de parâmetros flexíveis conforme a documentação da Omie
        public List<Map<String, Object>> param = new ArrayList<>();

        /**
         * Construtor utilitário para inicializar a requisição com parâmetros de paginação.
         * Normaliza a divergência de nomenclatura de parâmetros entre diferentes chamadas da Omie.
         */
        public OmieRequest(String call, String key, String secret, int pag) {
            this.call = call;
            this.appKey = key;
            this.appSecret = secret;

            Map<String, Object> p = new HashMap<>();

            // Tratamento de inconsistência da API Omie: 'pagina' para categorias, 'nPagina' para movimentos
            if ("ListarCategorias".equals(call)) {
                p.put("pagina", pag);
                p.put("registros_por_pagina", 500); // Maximizando o batch size para eficiência
            } else {
                p.put("nPagina", pag);
                p.put("nRegPorPagina", 500);
            }
            this.param.add(p);
        }
    }

    // --- DTOs de Resposta ---

    /**
     * Mapeia a resposta da listagem de categorias.
     */
    @Data
    public static class OmieCategoriaResponse {
        public Integer total_de_paginas;
        public List<Categoria> categoria_cadastro;
    }

    /**
     * Mapeia a resposta da listagem de movimentos financeiros.
     */
    @Data
    public static class OmieMovimentoResponse {
        public Integer nTotPaginas;
        public List<Movimento> movimentos;
    }

    /**
     * Representação de uma Categoria no plano de contas.
     */
    @Data
    public static class Categoria {
        public String codigo;
        public String descricao;

        // Mapeamento de objeto aninhado para acesso a dados do DRE
        @JsonProperty("dadosDRE") public DadosDRE dadosDRE;

        @Data public static class DadosDRE { public String descricaoDRE; }
    }

    /**
     * Representação de um Movimento Financeiro (Título).
     */
    @Data
    public static class Movimento {
        public Detalhes detalhes;
        public Resumo resumo;

        @Data
        public static class Detalhes {
            public String cCodCateg;    // Código da categoria para classificação
            public String dDtPagamento; // Data de liquidação
            public String dDtVenc;      // Data de vencimento
            public String cNumTitulo;   // Identificador/Descrição do título
        }

        @Data
        public static class Resumo {
            public Double nValPago;     // Valor liquidado
            public Double nValAberto;   // Valor pendente
        }
    }
}