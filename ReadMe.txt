EASY CHATBOT FINANCEIRO

Este repositório contém o backend do Assistente Virtual Financeiro desenvolvido pelo Squad 08. O sistema atua como um middleware inteligente entre a API do WhatsApp Business (Meta) e o ERP Omie, permitindo que clientes consultem faturas, visualizem relatórios financeiros (DRE) e interajam de forma automatizada e segura.



VISÃO GERAL DO PROJETO

O objetivo principal é democratizar o acesso a dados financeiros para clientes finais, eliminando a necessidade de login em portais complexos. O chatbot utiliza processamento de linguagem natural (regras de negócio) para entender a intenção do usuário e entregar resumos financeiros diretamente no WhatsApp.



PRINCIPAIS FUNCIONALIDADES

Webhook WhatsApp: Recebimento e processamento de mensagens em tempo real via API da Meta.
Menus Interativos: Navegação fluida utilizando botões e listas nativas do WhatsApp.
Relatórios Financeiros: Geração de resumos de Receitas, Despesas e Resultado (15, 30 dias ou personalizado).
Consulta de Faturas: Listagem de boletos e contas em aberto com vencimento e valores.
Segurança (Auth): Sistema de autenticação via CPF com gestão de sessão (Token válido por 24 horas).
Mock Mode: Camada de serviço preparada com dados simulados para demonstrações estáveis e testes de interface.



STACK TECNOLÓGICA

O projeto foi construído seguindo os padrões de mercado para aplicações Java Enterprise:

Linguagem: Java 21
Framework: Spring Boot 3.5.x
Gerenciamento de Dependências: Maven
Banco de Dados: MySQL 8.0
ORM: Spring Data JPA / Hibernate
Documentação: SpringDoc OpenAPI (Swagger UI)
Cliente HTTP: RestTemplate
Utilitários: Lombok



PRÉ REQUISITOS

Para executar este projeto localmente, certifique-se de ter instalado:

JDK 21 (Java Development Kit).
MySQL Server rodando na porta 3306.
Ngrok (para expor o localhost ao Webhook do Facebook).
IntelliJ IDEA (ou IDE de sua preferência).



COMO RODAR O PROJETO

1. Configuração do Banco de Dados
Crie um banco de dados MySQL chamado teste_squad8. O Hibernate se encarregará de criar as tabelas automaticamente (ddl-auto=update).

SQL
CREATE DATABASE teste_squad8;

2. Configuração de Credenciais
No arquivo src/main/resources/application.properties, configure as credenciais do banco e os tokens da API do WhatsApp:

Properties
# Banco de Dados
spring.datasource.url=jdbc:mysql://localhost:3306/teste_squad8
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_MYSQL

# WhatsApp API (Meta for Developers)
whatsapp.token=SEU_TOKEN_DE_ACESSO
whatsapp.phone-number-id=SEU_PHONE_NUMBER_ID
whatsapp.verify-token=TESTE_WEBHOOK


3. Expondo o Servidor (Webhook)
O Facebook precisa acessar seu servidor local. Utilize o Ngrok para criar um túnel:

Bash
ngrok http 3000

Copie a URL gerada (ex: https://xxxx-xxxx.ngrok-free.app) e configure no painel do Facebook Developers.

4. Executando a Aplicação
Via terminal (na raiz do projeto) ou pela IDE:

Bash
./mvnw spring-boot:run

O servidor iniciará na porta 3000.



DOCUMENTAÇÃO DA API (SWAGGER)

A API possui documentação automática via Swagger UI. Após iniciar a aplicação, acesse:

 http://localhost:3000/swagger-ui.html

Lá você encontrará todos os endpoints documentados, com exemplos de payload para requisição e resposta.



COMANDOS DE TESTE (CHATBOT)

Para facilitar a demonstração e os testes de fluxo, o bot possui comandos administrativos:

/reset: Limpa a sessão do usuário no banco de dados, forçando uma nova solicitação de CPF (ideal para reiniciar demos).

Fluxo Normal: Envie "Oi" para iniciar a interação.



ESTRUTURA DO PROJETO

config: Configurações de Beans (RestTemplate, Swagger).
controller: Endpoints REST (Webhook e Omie).
service: Regras de negócio, lógica de menus e integração externa.
repository: Camada de acesso a dados (DAO/JPA).
entitys: Modelos de dados (ORM).
dto: Objetos de transferência de dados (Mapeamento JSON).