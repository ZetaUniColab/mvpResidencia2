package com.easy.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsappService {

    private static final String API_URL = "https://graph.facebook.com/v22.0/{PHONE_NUMBER_ID}/messages";

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.token}")
    private String token;

    public String sendTextMessage(String to, String message) {

        RestTemplate restTemplate = new RestTemplate();

        String url = API_URL.replace("{PHONE_NUMBER_ID}", phoneNumberId);

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        body.put("type", "text");

        Map<String, String> text = new HashMap<>();
        text.put("body", message);
        body.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, String.class);
    }
}
