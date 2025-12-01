package com.easy.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Classe de configuração global para definição de Beans do Spring.
 * Centraliza a instanciação de objetos que serão injetados em outros componentes via Autowired.
 */
@Configuration
public class AppConfig {

    /**
     * Disponibiliza uma instância de RestTemplate no contexto da aplicação.
     * O RestTemplate é o cliente HTTP síncrono utilizado para realizar requisições
     * a APIs externas (como WhatsApp API e Omie API).
     *
     * @return Uma nova instância de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}