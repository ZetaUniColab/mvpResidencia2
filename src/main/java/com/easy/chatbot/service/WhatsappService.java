package com.easy.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappService {

    @Value("${whatsapp.access-token}")
    private String accessToken;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final OmieService omieService;
    private final ObjectMapper mapper = new ObjectMapper();

    public WhatsappService(RestTemplate restTemplate, OmieService omieService) {
        this.restTemplate = restTemplate;
        this.omieService = omieService;
    }

    public void processMessage(JsonNode messageData) {
        String from = messageData.get("from").asText();
        String type = messageData.get("type").asText();

        if ("text".equals(type)) {
            String msgBody = messageData.path("text").path("body").asText();
            System.out.println("Mensagem de TEXTO recebida de " + from + ": " + msgBody);
            sendMainMenu(from);

        } else if ("interactive".equals(type)) {
            JsonNode interactiveData = messageData.path("interactive");
            String interactiveType = interactiveData.path("type").asText();

            if ("list_reply".equals(interactiveType)) {
                String selectedId = interactiveData.path("list_reply").path("id").asText();
                System.out.println("Resposta de LISTA recebida de " + from + ". ID: " + selectedId);
                handleListReply(from, selectedId);

            } else if ("button_reply".equals(interactiveType)) {
                String selectedId = interactiveData.path("button_reply").path("id").asText();
                System.out.println("Resposta de BOTÃO recebida de " + from + ". ID: " + selectedId);
                sendTextMessage(from, "Você clicou no botão ID: " + selectedId);
            }
        } else {
            System.out.println("Tipo de mensagem não tratado: " + type);
            sendTextMessage(from, "Desculpe, só consigo processar texto e botões no momento.");
        }
    }

    private void handleListReply(String to, String selectedId) {
        try {
            switch (selectedId) {
                case "resumo_15_dias":
                    omieService.handleResumoRequest(to, 15, this);
                    break;
                case "resumo_30_dias":
                    omieService.handleResumoRequest(to, 30, this);
                    break;
                case "resumo_365_dias":
                    omieService.handleResumoRequest(to, 365, this);
                    break;
                case "servico_01":
                    sendTextMessage(to, "Você escolheu 'Consulta de Serviços'. Nossos serviços são A, B e C.");
                    break;
                case "servico_02":
                    sendTextMessage(to, "Você escolheu 'Contratar Serviço'. Para contratar, por favor, nos diga qual serviço você deseja.");
                    break;
                case "suporte_01":
                    sendTextMessage(to, "Você escolheu 'Falar com Atendente'. Estamos transferindo sua conversa para um de nossos especialistas. Aguarde um momento.");
                    break;
                case "suporte_02":
                    sendTextMessage(to, "Você escolheu 'Abrir Chamado'. Por favor, descreva o problema que você está enfrentando.");
                    break;
                case "fin_01":
                    sendTextMessage(to, "Você escolheu 'Segunda Via de Boleto'. Estamos gerando seu boleto e enviaremos em instantes.");
                    break;
                case "fin_02":
                    sendTextMessage(to, "Você escolheu 'Ver Faturas'. Consultando suas faturas em aberto...");
                    break;
                default:
                    sendTextMessage(to, "Opção não reconhecida. Enviando o menu principal novamente.");
                    sendMainMenu(to);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendTextMessage(to, "Ocorreu um erro ao processar sua solicitação.");
        }
    }

    public void sendTextMessage(String to, String text) {
        ObjectNode json = mapper.createObjectNode();
        json.put("messaging_product", "whatsapp");
        json.put("to", to);
        json.put("type", "text");
        json.set("text", mapper.createObjectNode().put("body", text));
        sendMessage(json);
    }

    public void sendMainMenu(String to) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("messaging_product", "whatsapp");
            root.put("to", to);
            root.put("type", "interactive");

            ObjectNode interactive = root.putObject("interactive");
            interactive.put("type", "list");
            interactive.putObject("header").put("type", "text").put("text", "🤖 Bem-vindo ao Atendimento");
            interactive.putObject("body").put("text", "Por favor, escolha uma das opções abaixo:");
            interactive.putObject("footer").put("text", "Atendimento Bot 24h");

            ObjectNode action = interactive.putObject("action");

            action.put("button", "Ver Opções");

            ArrayNode sections = action.putArray("sections");

            ObjectNode sec1 = sections.addObject();
            sec1.put("title", "1. Resumo Financeiro");
            ArrayNode rows1 = sec1.putArray("rows");
            rows1.addObject().put("id", "resumo_15_dias").put("title", "Últimos 15 dias");
            rows1.addObject().put("id", "resumo_30_dias").put("title", "Últimos 30 dias");
            rows1.addObject().put("id", "resumo_365_dias").put("title", "Últimos 365 dias");

            ObjectNode sec2 = sections.addObject();
            sec2.put("title", "2. Serviços");
            ArrayNode rows2 = sec2.putArray("rows");
            rows2.addObject().put("id", "servico_01").put("title", "Consulta de Serviços");
            rows2.addObject().put("id", "servico_02").put("title", "Contratar Serviço");

            ObjectNode sec3 = sections.addObject();
            sec3.put("title", "3. Suporte");
            ArrayNode rows3 = sec3.putArray("rows");
            rows3.addObject().put("id", "suporte_01").put("title", "Falar com Atendente");
            rows3.addObject().put("id", "suporte_02").put("title", "Abrir Chamado");

            ObjectNode sec4 = sections.addObject();
            sec4.put("title", "4. Financeiro");
            ArrayNode rows4 = sec4.putArray("rows");
            rows4.addObject().put("id", "fin_01").put("title", "Segunda Via de Boleto");
            rows4.addObject().put("id", "fin_02").put("title", "Ver Faturas");

            sendMessage(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ObjectNode jsonData) {
        String url = apiUrl + "/" + phoneNumberId + "/messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(jsonData.toString(), headers);

        try {
            restTemplate.postForObject(url, request, String.class);
            System.out.println("Mensagem enviada com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}
