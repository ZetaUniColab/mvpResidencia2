package com.easy.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal responsável pela inicialização da aplicação Spring Boot.
 * * A anotação @SpringBootApplication encapsula @Configuration, @EnableAutoConfiguration
 * e @ComponentScan, configurando automaticamente o contexto da aplicação baseando-se
 * nas dependências presentes no classpath.
 */
@SpringBootApplication
public class ChatbotBackendApplication {

    /**
     * Método main que inicia o container do Spring, configura o servidor web embarcado (Tomcat)
     * e inicializa todos os beans gerenciados.
     *
     * @param args Argumentos de linha de comando passados durante a execução.
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatbotBackendApplication.class, args);
    }
}