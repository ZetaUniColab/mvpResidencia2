package com.easy.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {


    @Bean
    public WebClient omieFinanceiroClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/financas/mf/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient omieCategoriaClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/geral/categorias/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
