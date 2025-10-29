package com.easy.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Serviço responsável por se comunicar com as APIs da Omie.
 * Ele usa os dois WebClients configurados na classe WebClientConfig.
 */
@Service
public class OmieIntegrationService {

    private final WebClient omieFinanceiroClient;
    private final WebClient omieCategoriaClient;

    private final String appKey = "5614700718627";
    private final String appSecret = "2ae8328ce879960d99ba83e7986805a3";

    public OmieIntegrationService(WebClient omieFinanceiroClient,
                                  WebClient omieCategoriaClient) {
        this.omieFinanceiroClient = omieFinanceiroClient;
        this.omieCategoriaClient = omieCategoriaClient;
    }

    /** Chama a API de movimentos financeiros (contas a pagar/receber) */
    public Mono<String> listarMovimentos() {
        Map<String, Object> body = Map.of(
                "call", "ListarMovimentos",
                "app_key", appKey,
                "app_secret", appSecret,
                "param", new Object[]{
                        Map.of("nPagina", 1, "nRegPorPagina", 10)
                }
        );

        return omieFinanceiroClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** Chama a API de categorias da Omie */
    public Mono<String> listarCategorias() {
        Map<String, Object> body = Map.of(
                "call", "ListarCategorias",
                "app_key", appKey,
                "app_secret", appSecret,
                "param", new Object[]{
                        Map.of("pagina", 1, "registros_por_pagina", 10)
                }
        );

        return omieCategoriaClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    /** Faz as duas chamadas ao mesmo tempo (em paralelo) */
    public Mono<Map<String, String>> listarAmbas() {
        return Mono.zip(listarMovimentos(), listarCategorias())
                .map(tuple -> Map.of(
                        "movimentos", tuple.getT1(),
                        "categorias", tuple.getT2()
                ));
    }
}
