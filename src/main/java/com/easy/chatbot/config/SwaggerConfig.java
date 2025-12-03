package com.easy.chatbot.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração personalizada para o swager UI.
 * Define metadados da API, servidores de destino e informações de contato
 * para facilitar o consumo e teste dos endpoints por desenvolvedores.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Constrói o objeto OpenAPI com as definições do projeto.
     * Inclui configurações de ambiente e informações detalhadas sobre a API.
     *
     * @return Objeto OpenAPI configurado.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Define as URLs base para testes direto na interface do Swagger
                .servers(List.of(
                        new Server().url("http://localhost:3000").description("Ambiente de Desenvolvimento (Local)"),
                        new Server().url("https://api.seudominio.com").description("Ambiente de Produção")
                ))
                .info(new Info()
                        .title("Easy Chatbot Financeiro API")
                        .version("1.0.0-BETA")
                        .description(descricaoDetalhada())
                        .termsOfService("https://easyfinance.com.br/termos")
                        .contact(new Contact()
                                .name("Suporte Técnico - Squad 08")
                                .email("dev@easyfinance.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Manual de Integração Omie")
                        .url("https://developer.omie.com.br"));
    }

    /*
            * Gera a descrição da API utilizando Text Blocks para formatação Markdown.
     * @return String com a descrição formatada.
     */
    private String descricaoDetalhada() {
        return """
                ### Visão Geral
                API responsável pela lógica de negócio do Chatbot Financeiro, integrando WhatsApp Business API e ERP Omie.
                Permite consulta de faturas, geração de relatórios financeiros e autenticação de clientes.
                
                ### Funcionalidades
                * **Webhook:** Processamento em tempo real de mensagens recebidas.
                * **Integração ERP:** Consulta de dados financeiros (contas a pagar/receber).
                * **Segurança:** Controle de sessão via CPF com expiração automática (24h).
                
                ### Observações
                * Ambiente configurado com dados simulados (MOCK) para demonstração de funcionalidades.
                """;
    }
}