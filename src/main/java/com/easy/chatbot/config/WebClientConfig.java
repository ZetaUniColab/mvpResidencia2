package com.easy.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    // API de movimentos financeiros (Contas a pagar/receber)
    @Bean
    public WebClient omieFinanceiroClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/financas/mf/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // API de categorias (usada para buscar nomes das categorias)
    @Bean
    public WebClient omieCategoriaClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/geral/categorias/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
