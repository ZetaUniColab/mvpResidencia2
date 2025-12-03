package com.easy.chatbot.service;

import com.easy.chatbot.entitys.Cliente;
import com.easy.chatbot.repository.ClienteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Serviço central de msg:
 * 1- Processa webhooks recebidos do WhatsApp.
 * 2 - Gerencia fluxo de autenticação e sessão do usuário.
 * 3- Controla a navegação entre menus.
 * 4- Dispara mensagens de resposta via API do Facebook.
 */
@Service
public class WhatsappService {

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.token}")
    private String token;

    @Value("${whatsapp.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OmieService omieService;
    private final ClienteRepository clienteRepository;

    /**
     * Construtor com injeção de dependências.
     * Utiliza @Lazy no OmieService para prevenir dependência circular durante a inicialização do contexto.
     */
    public WhatsappService(RestTemplate restTemplate,
                           @Lazy OmieService omieService,
                           ClienteRepository clienteRepository) {
        this.restTemplate = restTemplate;
        this.omieService = omieService;
        this.clienteRepository = clienteRepository;
    }

    // --- Métodos de Envio ---

    public String sendTextMessage(String to, String message) {
        try {
            ObjectNode json = mapper.createObjectNode();
            json.put("messaging_product", "whatsapp");
            json.put("to", to);
            json.put("type", "text");
            json.set("text", mapper.createObjectNode().put("body", message));
            return sendJson(json);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /**
     * constrói e envia o Menu Principal utilizando botões interativos.
     * melhora a UX permitindo resposta rápida com um clique.
     */
    public void sendMainMenu(String to) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("messaging_product", "whatsapp");
            root.put("to", to);
            root.put("type", "interactive");

            ObjectNode interactive = root.putObject("interactive");
            interactive.put("type", "button");
            interactive.putObject("header").put("type", "text").put("text", "🤖 Menu Principal");
            interactive.putObject("body").put("text", "Olá! Como posso te ajudar com suas finanças hoje?");
            interactive.putObject("footer").put("text", "Selecione uma opção:");

            ObjectNode action = interactive.putObject("action");
            ArrayNode buttons = action.putArray("buttons");

            ObjectNode btn1 = buttons.addObject();
            btn1.put("type", "reply");
            btn1.putObject("reply").put("id", "menu_resumo").put("title", "📊 Resumo Financeiro");

            ObjectNode btn2 = buttons.addObject();
            btn2.put("type", "reply");
            btn2.putObject("reply").put("id", "menu_faturas").put("title", "📄 Ver Faturas");

            sendJson(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Constrói e manda o Submenu de seleção de período utilizando lista interativa.
     */
    public void sendResumoSubMenu(String to) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("messaging_product", "whatsapp");
            root.put("to", to);
            root.put("type", "interactive");

            ObjectNode interactive = root.putObject("interactive");
            interactive.put("type", "list");
            interactive.putObject("header").put("type", "text").put("text", "📅 Período do Relatório");
            interactive.putObject("body").put("text", "Selecione o período que deseja analisar:");
            interactive.putObject("footer").put("text", "Easy Finance");

            ObjectNode action = interactive.putObject("action");
            action.put("button", "Escolher Período");

            ArrayNode sections = action.putArray("sections");
            ObjectNode sec1 = sections.addObject();
            sec1.put("title", "Opções Rápidas");

            ArrayNode rows = sec1.putArray("rows");
            rows.addObject().put("id", "resumo_15").put("title", "Últimos 15 dias");
            rows.addObject().put("id", "resumo_30").put("title", "Últimos 30 dias");
            rows.addObject().put("id", "resumo_custom").put("title", "📅 Data Personalizada").put("description", "Ex: 01/01/2025");

            sendJson(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Executa a chamada HTTP POST para a API do Facebook.
     */
    private String sendJson(ObjectNode json) {
        String url = apiUrl + "/" + phoneNumberId + "/messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    // --- Processamento de Mensagens ---

    /**
     * Método principal de processamento.
     * analisa o payload do webhook, normaliza dados, verifica autenticação e roteia para a ação correta.
     */
    public void processMessage(JsonNode messageData) {
        String from = messageData.get("from").asText();
        String type = messageData.get("type").asText();

        // ajeita o numero pro formato br,  insere o 9º dígito se o formato for DDI+DDD+8dígitos (ex: 55+11+88887777)
        if (from.startsWith("55") && from.length() == 12) {
            from = from.substring(0, 4) + "9" + from.substring(4);
        }

        if ("text".equals(type)) {
            String textBody = messageData.path("text").path("body").asText().trim();

            // comando administrativo para reset de sessão (só para poder ficar testando se ta funfanfo, pode apagar e funciona deboa)
            if ("/reset".equalsIgnoreCase(textBody)) {
                Optional<Cliente> cli = clienteRepository.findByWhatsapp(from);
                if (cli.isPresent()) {
                    Cliente c = cli.get();
                    c.setDataValidadeToken(null); // Invalida o token
                    clienteRepository.save(c);
                    sendTextMessage(from, "🔄 Sessão Resetada! Envie 'Oi'.");
                }
                return;
            }

            // validação formato de data para relatório personalizado
            if (textBody.matches("\\d{2}/\\d{2}/\\d{4}")) {
                try {
                    LocalDate dataInicio = LocalDate.parse(textBody, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate hoje = LocalDate.now();

                    if (dataInicio.isAfter(hoje)) {
                        sendTextMessage(from, "⚠️ Data futura inválida. Escolha uma data passada.");
                    } else {
                        omieService.handleResumoPorPeriodo(from, dataInicio, hoje, this);
                    }
                } catch (Exception e) {
                    sendTextMessage(from, "❌ Data inválida. Use o formato dd/mm/aaaa.");
                }
                return;
            }

            // verifica autenticação antes de mostrar o menu
            if (verificarAutenticacao(from, textBody)) {
                sendMainMenu(from);
            }

        } else if ("interactive".equals(type)) {
            // processamento de resposta a botões ou listas
            JsonNode interactive = messageData.path("interactive");
            String subType = interactive.path("type").asText();
            String id = "";

            if ("list_reply".equals(subType)) {
                id = interactive.path("list_reply").path("id").asText();
            } else if ("button_reply".equals(subType)) {
                id = interactive.path("button_reply").path("id").asText();
            }

            handleMenuOption(from, id);
        }
    }

    /**
     * Valida a sessão do usuário.
     * - Se existir e for válida: Permite acesso.
     * - Se n tiver Solicita CPF para autenticação.
     *
     * @return true se autenticado, false se bloqueado.
     */
    private boolean verificarAutenticacao(String whatsapp, String textoRecebido) {
        Optional<Cliente> clienteOpt = clienteRepository.findByWhatsapp(whatsapp);
        Cliente cliente;

        if (clienteOpt.isEmpty()) {
            cliente = new Cliente("Novo Usuário", whatsapp);
            clienteRepository.save(cliente);
        } else {
            cliente = clienteOpt.get();
        }

        // a validação do token de 24h
        if (cliente.getDataValidadeToken() != null && cliente.getDataValidadeToken().isAfter(LocalDateTime.now())) {
            return true;
        }

        // aq valida formato simples d CPF (apenas números e 11 dígitos)
        String apenasNumeros = textoRecebido.replaceAll("\\D", "");
        if (apenasNumeros.length() == 11) {
            cliente.setCpf(apenasNumeros);
            cliente.setDataValidadeToken(LocalDateTime.now().plusHours(24));
            clienteRepository.save(cliente);
            sendTextMessage(whatsapp, "✅ *Acesso Liberado!* Sessão válida por 24h.");
            return true;
        } else {
            sendTextMessage(whatsapp, "🔒 *Segurança Easy*\n\nSua sessão expirou. Por favor, digite seu *CPF*.");
            return false;
        }
    }
     /* Roteador central de comandos do menu de esolhas.*/

    private void handleMenuOption(String to, String optionId) {
        switch (optionId) {
            case "menu_resumo":
                sendResumoSubMenu(to);
                break;
            case "menu_faturas":
                omieService.handleFaturasPendentes(to, this);
                break;
            case "resumo_15":
                omieService.handleResumoRequest(to, 15, this);
                break;
            case "resumo_30":
                omieService.handleResumoRequest(to, 30, this);
                break;
            case "resumo_custom":
                sendTextMessage(to, "🗓️ *Data Personalizada*\n\nPor favor, digite a data inicial desejada no formato:\n*dd/mm/aaaa*");
                break;
            default:
                sendTextMessage(to, "Opção desconhecida. Digite 'oi' pra reiniciar.");
                sendMainMenu(to);
        }
    }
}