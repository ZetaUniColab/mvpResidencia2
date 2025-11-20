package com.easy.chatbot.controller;

import com.easy.chatbot.service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

    @Autowired
    private WhatsappService service;

    @PostMapping("/send")
    public String sendMessage() {

        String numeroDestino = "5587996261503";
        String mensagem = "Olá, esta mensagem foi enviada automaticamente!";
        //endpoint de teste: POST http://localhost:8080/whatsapp/send

        return service.sendTextMessage(numeroDestino, mensagem);
    }
}



