package com.easy.chatbot.service;

import com.easy.chatbot.dto.OmieDTOs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Serviço responsável pela lógica de negócios financeira e integração com o ERP Omie.
 * Gerencia a obtenção, processamento e formatação dos dados financeiros para apresentação ao usuário.
 * * Nota: Atualmente implementa mocks (simulações) para garantir estabilidade em demonstrações.
 */
@Service
public class OmieService {

    @Value("${omie.app-key}")
    private String appKey;

    @Value("${omie.app-secret}")
    private String appSecret;

    @Value("${omie.api-url}")
    private String omieApiUrl;

    private final RestTemplate restTemplate;

    // Mapa estático para tradução de códigos de categoria contábil para descrições amigáveis
    private static final Map<String, String> MAPA_CODIGO_GERENCIAL = new LinkedHashMap<>();
    static {
        MAPA_CODIGO_GERENCIAL.put("1.0", "Receitas Operacionais");
        MAPA_CODIGO_GERENCIAL.put("2.1", "Custos Variáveis");
        MAPA_CODIGO_GERENCIAL.put("3.0", "Despesas com Pessoal");
        MAPA_CODIGO_GERENCIAL.put("3.1", "Despesas Administrativas");
        MAPA_CODIGO_GERENCIAL.put("4.0", "Investimentos");
    }

    public OmieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gera e envia um relatório de faturas pendentes.
     * Utiliza dados simulados para garantir retorno visual consistente.
     *
     * @param to Número de destino (WhatsApp).
     * @param whatsAppService Serviço de mensageria para envio da resposta.
     */
    public void handleFaturasPendentes(String to, WhatsappService whatsAppService) {
        whatsAppService.sendTextMessage(to, "🔍 Consultando faturas em aberto...");

        // Simulação de latência de rede
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("📄 *Faturas em Aberto*\n\n");
        relatorio.append("🔸 *Fornecedor Tech Solutions*\n   📅 Vence: 05/12/2025\n   💰 Valor: R$ 1.250,00\n\n");
        relatorio.append("🔸 *Aluguel Sala 02*\n   📅 Vence: 10/12/2025\n   💰 Valor: R$ 2.450,00\n\n");
        relatorio.append("⚠️ *Total Pendente: R$ 3.700,00*");

        whatsAppService.sendTextMessage(to, relatorio.toString());
    }

    /**
     * Processa a solicitação de resumo financeiro baseada em um intervalo de datas.
     * Calcula a diferença de dias e gera valores proporcionais para o relatório.
     *
     * @param to Número de destino.
     * @param inicio Data inicial do período.
     * @param fim Data final do período.
     * @param whatsAppService Serviço de mensageria.
     */
    public void handleResumoPorPeriodo(String to, LocalDate inicio, LocalDate fim, WhatsappService whatsAppService) {
        long dias = ChronoUnit.DAYS.between(inicio, fim);
        if (dias == 0) dias = 1; // Previne inconsistência matemática em intervalos de mesmo dia

        whatsAppService.sendTextMessage(to, "⏳ Gerando relatório de " + inicio.format(DateTimeFormatter.ofPattern("dd/MM")) + " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM")) + "...");

        // Obtém dados simulados baseados na amplitude do período
        Map<String, Double> resultados = gerarRelatorioMock((int) dias);

        double totalReceitas = 0;
        double totalDespesas = 0;
        StringBuilder detalhes = new StringBuilder();
        NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        // Processamento de totais e formatação de linhas
        for (Map.Entry<String, Double> entry : resultados.entrySet()) {
            double valor = entry.getValue();
            String codigo = entry.getKey();
            String descricao = MAPA_CODIGO_GERENCIAL.getOrDefault(codigo, codigo);

            if (valor > 0) {
                detalhes.append("- ").append(descricao).append(": *").append(formatador.format(valor)).append("*\n");
            }

            // Classificação básica: Códigos iniciados em 1 ou 6 são Receitas, demais são Despesas
            if (codigo.startsWith("1.") || codigo.startsWith("6.")) {
                totalReceitas += valor;
            } else {
                totalDespesas += valor;
            }
        }

        double liquido = totalReceitas - totalDespesas;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Construção do payload da mensagem
        StringBuilder msg = new StringBuilder();
        msg.append("📊 *Resumo Financeiro (").append(dias).append(" dias)*\n");
        msg.append("🗓️ Período: ").append(inicio.format(dtf)).append(" a ").append(fim.format(dtf)).append("\n\n");
        msg.append("✅ Receitas: *").append(formatador.format(totalReceitas)).append("*\n");
        msg.append("🔻 Despesas: *").append(formatador.format(totalDespesas)).append("*\n");

        String emojiResult = liquido >= 0 ? "🤑" : "⚠️";
        msg.append(emojiResult + " *Resultado: ").append(formatador.format(liquido)).append("*\n\n");
        msg.append("\n*Detalhes por Categoria:*\n").append(detalhes);

        whatsAppService.sendTextMessage(to, msg.toString());
    }

    /**
     * Wrapper para facilitar chamadas com períodos pré-definidos (ex: 15 ou 30 dias).
     */
    public void handleResumoRequest(String to, int dias, WhatsappService whatsAppService) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(dias);
        handleResumoPorPeriodo(to, inicio, fim, whatsAppService);
    }

    /**
     * Gera dados financeiros simulados.
     * Utiliza a quantidade de dias como fator multiplicador para garantir verossimilhança nos valores.
     *
     * @param dias Quantidade de dias do período para cálculo proporcional.
     * @return Mapa contendo código da categoria e valor calculado.
     */
    private Map<String, Double> gerarRelatorioMock(int dias) {
        Map<String, Double> dados = new LinkedHashMap<>();
        double fator = dias > 0 ? dias : 1;

        dados.put("1.0", 1500.00 * fator);
        dados.put("2.1", 350.00 * fator);
        dados.put("3.0", 400.00 * fator);
        dados.put("3.1", 100.00 * fator);
        dados.put("4.0", 50.00 * fator);

        return dados;
    }

    // Métodos stubs para manter compatibilidade de compilação com chamadas legadas
    public List<Movimento> buscarMovimentosPaginado() { return new ArrayList<>(); }
    public List<Categoria> buscarCategoriasPaginado() { return new ArrayList<>(); }
    public reactor.core.publisher.Mono<String> listarMovimentos() { return reactor.core.publisher.Mono.empty(); }
    public reactor.core.publisher.Mono<String> listarCategorias() { return reactor.core.publisher.Mono.empty(); }
    public reactor.core.publisher.Mono<Map<String, String>> listarAmbas() { return reactor.core.publisher.Mono.empty(); }
}