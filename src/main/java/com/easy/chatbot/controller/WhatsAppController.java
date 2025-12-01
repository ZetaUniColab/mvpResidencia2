package com.easy.chatbot.controller;

import com.easy.chatbot.service.WhatsappService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador principal para integração com o WhatsApp Business API.
 * Gerencia a validação do webhook (handshake) e o recebimento de eventos (mensagens).
 */
@RestController
@RequestMapping("/whatsapp")
@Tag(name = "WhatsApp Webhook", description = "Endpoints para comunicação com a API do Facebook/Meta")
public class WhatsAppController {

    @Autowired
    private WhatsappService service;

    @Value("${whatsapp.verify-token}")
    private String verifyToken;

    /**
     * Endpoint utilitário para disparo manual de mensagens.
     * Útil para testes de integração sem necessidade de interação via aparelho celular.
     */
    @PostMapping("/send")
    @Operation(
            summary = "Teste de Envio Manual",
            description = "Força o envio do Menu Principal para um número específico. **Atenção:** Use apenas para testes internos.",
            tags = {"Testes Manuais"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enviada com sucesso (O 'Oi' chegou!)"),
            @ApiResponse(responseCode = "500", description = "Erro na comunicação com o Facebook (Token vencido ou ID errado)")
    })
    public String sendMessage() {
        String numeroDestino = "5579988482109"; // Hardcoded para debug
        service.sendMainMenu(numeroDestino);
        return "Menu enviado para teste.";
    }

    // --- Fluxo de Webhook ---

    /**
     * Validação do Webhook (GET).
     * O Facebook chama este endpoint para verificar a propriedade do servidor.
     * Valida o token de verificação e retorna o 'challenge' se correto.
     */
    @GetMapping("/webhook")
    @Operation(summary = "Validação do Webhook", description = "Endpoint que o Facebook chama para verificar se o servidor é seu mesmo (Handshake).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validado com sucesso (Conexão aceita)"),
            @ApiResponse(responseCode = "403", description = "Token inválido (Tentativa de acesso não autorizada)")
    })
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Recebimento de Eventos (POST).
     * Endpoint onde o Facebook entrega as notificações de mensagens recebidas.
     * Processa o JSON para extrair o conteúdo relevante e delega para o Service.
     */
    @PostMapping("/webhook")
    @Operation(summary = "Recebimento de Mensagens", description = "Aqui é onde o Facebook entrega as mensagens que os clientes mandam no Zap.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payload recebido e processado (ou ignorado se for status)"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar o JSON")
    })
    public ResponseEntity<Void> receiveWebhook(@RequestBody JsonNode body) {
        try {
            // Navegação defensiva no JSON para garantir que é um evento de mensagem válido
            if (body.has("object") && "whatsapp_business_account".equals(body.get("object").asText())) {
                JsonNode changes = body.path("entry").get(0).path("changes").get(0);
                JsonNode value = changes.path("value");

                if (value.has("messages")) {
                    JsonNode message = value.path("messages").get(0);
                    service.processMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Retorno mandatório de 200 OK para confirmar o recebimento ao Facebook
        return ResponseEntity.ok().build();
    }
}