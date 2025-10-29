package com.easy.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

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

    public Mono<Map<String, String>> listarAmbas() {
        return Mono.zip(listarMovimentos(), listarCategorias())
                .map(tuple -> Map.of(
                        "movimentos", tuple.getT1(),
                        "categorias", tuple.getT2()
                ));
    }
}
