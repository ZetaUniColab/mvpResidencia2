package com.easy.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Classe de configuração para clientes HTTP reativos (Non-blocking I/O).
 * Diferente do RestTemplate (que é síncrono/bloqueante), o WebClient opera de forma assíncrona,
 * ideal para cenários de alta concorrência ou quando se deseja evitar o bloqueio de threads
 * durante chamadas de rede lentas.
 * * Esta configuração define clientes pré-configurados para diferentes contextos da API da Omie.
 */
@Configuration
public class WebClientConfig {

    /**
     * Cria e configura uma instância de WebClient dedicada ao módulo financeiro.
     * Define a URL base para os endpoints de "Movimentos Financeiros" da Omie,
     * evitando a repetição de URIs e cabeçalhos em cada chamada.
     *
     * @return WebClient configurado para operações financeiras.
     */
    @Bean
    public WebClient omieFinanceiroClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/financas/mf/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Cria e configura uma instância de WebClient dedicada ao módulo de cadastros gerais.
     * Segrega a configuração para endpoints de "Categorias", mantendo a organização
     * e facilitando a manutenção caso as URLs base mudem independentemente.
     *
     * @return WebClient configurado para operações de categorias/geral.
     */
    @Bean
    public WebClient omieCategoriaClient() {
        return WebClient.builder()
                .baseUrl("https://app.omie.com.br/api/v1/geral/categorias/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}