package com.easy.chatbot.controller;

import com.easy.chatbot.service.WhatsappService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

    @Value("${whatsapp.verify-token}")
    private String verifyToken;

    private final WhatsappService whatsAppService;

    public WhatsAppController(WhatsappService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    // Validação do Webhook (GET)
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.verify_token") String token,
            @RequestParam(name = "hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Recebimento de Mensagens (POST)
    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody JsonNode body) {
        System.out.println("Requisição recebida: " + body.toPrettyString());

        try {
            if (body.has("object") && "whatsapp_business_account".equals(body.get("object").asText())) {
                JsonNode entry = body.path("entry").get(0);
                JsonNode changes = entry.path("changes").get(0);
                JsonNode value = changes.path("value");

                if (changes.path("field").asText().equals("messages") && value.has("messages")) {
                    JsonNode messageData = value.path("messages").get(0);

                    if (messageData.isMissingNode() || !messageData.has("from")) {
                        throw new IllegalArgumentException("Payload de mensagem inválido.");
                    }

                    whatsAppService.processMessage(messageData);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar webhook: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }
}



