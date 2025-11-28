package com.easy.chatbot.service;

import io.github.cdimascio.dotenv.Dotenv;// Importe a lib
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OmieIntegrationService {

    private final WebClient omieFinanceiroClient;
    private final WebClient omieCategoriaClient;

    // Removemos os valores fixos, agora são apenas declarados
    private final String appKey;
    private final String appSecret;

    public OmieIntegrationService(WebClient omieFinanceiroClient,
                                  WebClient omieCategoriaClient) {
        this.omieFinanceiroClient = omieFinanceiroClient;
        this.omieCategoriaClient = omieCategoriaClient;

        // Carrega as variáveis do arquivo .env
        // O parametro ignoreIfMissing(true) evita erro se o arquivo não existir (útil em produção se usar variáveis de ambiente do sistema)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Pega os valores. Se não achar no .env, tenta pegar das variáveis de sistema (bom para deploy)
        this.appKey = dotenv.get("OMIE_APP_KEY");
        this.appSecret = dotenv.get("OMIE_APP_SECRET");

        // Verificação de segurança simples (opcional)
        if (this.appKey == null || this.appSecret == null) {
            throw new RuntimeException("ERRO: As chaves da API Omie não foram encontradas no .env");
        }
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