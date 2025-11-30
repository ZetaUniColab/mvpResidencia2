package com.easy.chatbot.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OmieService {

    @Value("${omie.app-key}")
    private String appKey;

    @Value("${omie.app-secret}")
    private String appSecret;

    @Value("${omie.api-url}")
    private String omieApiUrl;

    private final RestTemplate restTemplate;

    // Constantes e Mapas
    private static final Map<String, String> MAPA_CODIGO_GERENCIAL = new LinkedHashMap<>();
    private static final Map<String, String> MAPA_DESCRICOES_RESPOSTA = new HashMap<>();

    static {
        MAPA_CODIGO_GERENCIAL.put("1.0", "1.0 - Receitas Operacionais");
        MAPA_CODIGO_GERENCIAL.put("2.1", "2.1 - Custos Variáveis");
        MAPA_CODIGO_GERENCIAL.put("3.0", "3.0 - Despesas com Pessoal");
        MAPA_CODIGO_GERENCIAL.put("3.1", "3.1 - Despesas Administrativas");
        MAPA_CODIGO_GERENCIAL.put("3.2", "3.2 - Pro-Labore");
        MAPA_CODIGO_GERENCIAL.put("4.0", "4.0 - Investimentos");
        MAPA_CODIGO_GERENCIAL.put("5.0", "5.0 - Parcelamentos");
        MAPA_CODIGO_GERENCIAL.put("6.0", "6.0 - Entradas Não Operacionais");
        MAPA_CODIGO_GERENCIAL.put("7.0", "7.0 - Saídas Não Operacionais");

        MAPA_DESCRICOES_RESPOSTA.putAll(MAPA_CODIGO_GERENCIAL);
    }

    public OmieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void handleResumoRequest(String to, int dias, WhatsappService whatsAppService) {
        whatsAppService.sendTextMessage(to, "Aguarde um momento... ⏳\nEstou gerando seu relatório financeiro dos últimos " + dias + " dias.");

        try {
            Map<String, Double> resultadosNumericos = gerarRelatorioFinanceiroGeral(dias);

            double totalReceitas = 0;
            double totalDespesasCustos = 0;
            Map<String, String> detalhesFormatados = new LinkedHashMap<>();
            NumberFormat formatadorReais = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (String codigo : resultadosNumericos.keySet()) {
                double valor = resultadosNumericos.get(codigo);
                String descricaoChave = MAPA_DESCRICOES_RESPOSTA.getOrDefault(codigo, codigo);
                detalhesFormatados.put(descricaoChave, formatadorReais.format(valor));

                if (ehReceita(codigo)) {
                    totalReceitas += valor;
                } else if (ehDespesaOuCusto(codigo)) {
                    totalDespesasCustos += valor;
                }
            }

            double resultadoLiquido = totalReceitas - totalDespesasCustos;
            LocalDate dataFim = LocalDate.now();
            LocalDate dataInicio = dataFim.minusDays(dias);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            StringBuilder resposta = new StringBuilder();
            resposta.append("📊 *Resumo Financeiro (Últimos ").append(dias).append(" dias)*\n");
            resposta.append("Período: ").append(dataInicio.format(dtf)).append(" a ").append(dataFim.format(dtf)).append("\n\n");

            resposta.append("*Resumo Geral:*\n");
            resposta.append("✅ Total de Receitas: *").append(formatadorReais.format(totalReceitas)).append("*\n");
            resposta.append("🔻 Total de Despesas/Custos: *").append(formatadorReais.format(totalDespesasCustos)).append("*\n");
            resposta.append("💰 Resultado Líquido: *").append(formatadorReais.format(resultadoLiquido)).append("*\n\n");

            resposta.append("*Detalhes por Categoria:*\n");
            for (Map.Entry<String, String> entry : detalhesFormatados.entrySet()) {
                if (!entry.getValue().equals(formatadorReais.format(0))) {
                    resposta.append("- ").append(entry.getKey()).append(": *").append(entry.getValue()).append("*\n");
                }
            }

            whatsAppService.sendTextMessage(to, resposta.toString());

        } catch (Exception e) {
            e.printStackTrace();
            whatsAppService.sendTextMessage(to, "❌ Erro ao consultar o relatório financeiro (API Omie).");
        }
    }

    private Map<String, Double> gerarRelatorioFinanceiroGeral(int dias) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(dias);

        List<Categoria> categorias = buscarCategoriasPaginado();
        List<Movimento> movimentos = buscarMovimentosPaginado();

        Map<String, String> mapaClassificacao = processarCategorias(categorias);

        Map<String, Double> resultados = new HashMap<>();
        for(String key : MAPA_CODIGO_GERENCIAL.keySet()) resultados.put(key, 0.0);

        for (Movimento mov : movimentos) {
            if (mov.detalhes == null || mov.detalhes.cCodCateg == null || mov.detalhes.dDtPagamento == null) continue;

            try {
                LocalDate dataPagamento = LocalDate.parse(mov.detalhes.dDtPagamento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (dataPagamento.isBefore(inicio) || dataPagamento.isAfter(fim)) continue;

                String codigoGrupo = mapaClassificacao.get(mov.detalhes.cCodCateg);
                if (codigoGrupo != null && resultados.containsKey(codigoGrupo)) {
                    resultados.put(codigoGrupo, resultados.get(codigoGrupo) + mov.resumo.nValPago);
                }
            } catch (Exception e) {
                // Data inválida, ignora
            }
        }
        return resultados;
    }

    private List<Categoria> buscarCategoriasPaginado() {
        List<Categoria> todas = new ArrayList<>();
        int pagina = 1;
        int totalPaginas = 1;

        do {
            OmieRequest req = new OmieRequest("ListarCategorias", appKey, appSecret, pagina);
            OmieCategoriaResponse resp = restTemplate.postForObject(omieApiUrl + "/geral/categorias/", req, OmieCategoriaResponse.class);

            if (resp != null && resp.categoria_cadastro != null) {
                todas.addAll(resp.categoria_cadastro);
                totalPaginas = resp.total_de_paginas != null ? resp.total_de_paginas : 1;
            } else break;
            pagina++;
        } while (pagina <= totalPaginas);
        return todas;
    }

    private List<Movimento> buscarMovimentosPaginado() {
        List<Movimento> todos = new ArrayList<>();
        int pagina = 1;
        int totalPaginas = 1;

        do {
            OmieRequest req = new OmieRequest("ListarMovimentos", appKey, appSecret, pagina);
            OmieMovimentoResponse resp = restTemplate.postForObject(omieApiUrl + "/financas/mf/", req, OmieMovimentoResponse.class);

            if (resp != null && resp.movimentos != null) {
                todos.addAll(resp.movimentos);
                totalPaginas = resp.nTotPaginas != null ? resp.nTotPaginas : 1;
            } else break;
            pagina++;
        } while (pagina <= totalPaginas);
        return todos;
    }

    private Map<String, String> processarCategorias(List<Categoria> categorias) {
        Map<String, String> mapa = new HashMap<>();
        Pattern pattern = Pattern.compile("^(\\d+(\\.\\d+)?)");

        for (Categoria cat : categorias) {
            if (cat.codigo == null) continue;
            String codigoPrincipal = null;
            String desc = cat.descricao != null ? cat.descricao : "";
            String descDRE = cat.dadosDRE != null ? cat.dadosDRE.descricaoDRE : null;

            if (descDRE != null) {
                for (String cod : MAPA_CODIGO_GERENCIAL.keySet()) {
                    if (descDRE.startsWith(cod) || descDRE.equals(MAPA_CODIGO_GERENCIAL.get(cod))) {
                        codigoPrincipal = cod;
                        break;
                    }
                }
            }

            if (codigoPrincipal == null) {
                Matcher m = pattern.matcher(desc);
                if (m.find()) {
                    String codEnc = m.group(1);
                    if (MAPA_CODIGO_GERENCIAL.containsKey(codEnc)) {
                        codigoPrincipal = codEnc;
                    } else if (codEnc.contains(".")) {
                        String pai = codEnc.substring(0, codEnc.lastIndexOf("."));
                        if (MAPA_CODIGO_GERENCIAL.containsKey(pai)) codigoPrincipal = pai;
                    }
                }
            }

            if (codigoPrincipal != null) mapa.put(cat.codigo, codigoPrincipal);
        }
        return mapa;
    }

    private boolean ehReceita(String codigo) {
        return codigo.startsWith("1.") || codigo.startsWith("6.");
    }

    private boolean ehDespesaOuCusto(String codigo) {
        return codigo.startsWith("2.") || codigo.startsWith("3.") || codigo.startsWith("5.") || codigo.startsWith("7.");
    }


    @Data
    public static class OmieRequest {
        public String call;
        @JsonProperty("app_key") public String appKey;
        @JsonProperty("app_secret") public String appSecret;
        public List<Map<String, Object>> param = new ArrayList<>();

        public OmieRequest(String call, String key, String secret, int pag) {
            this.call = call;
            this.appKey = key;
            this.appSecret = secret;
            Map<String, Object> p = new HashMap<>();

            if ("ListarCategorias".equals(call)) {
                p.put("pagina", pag);
                p.put("registros_por_pagina", 500);
            } else {
                p.put("nPagina", pag);
                p.put("nRegPorPagina", 500);
            }

            this.param.add(p);
        }
    }

    @Data
    public static class OmieCategoriaResponse {
        public Integer total_de_paginas;
        public List<Categoria> categoria_cadastro;
    }

    @Data
    public static class OmieMovimentoResponse {
        public Integer nTotPaginas;
        public List<Movimento> movimentos;
    }

    @Data
    public static class Categoria {
        public String codigo;
        public String descricao;
        @JsonProperty("dadosDRE") public DadosDRE dadosDRE;
        @Data public static class DadosDRE { public String descricaoDRE; }
    }

    @Data
    public static class Movimento {
        public Detalhes detalhes;
        public Resumo resumo;
        @Data public static class Detalhes { public String cCodCateg; public String dDtPagamento; }
        @Data public static class Resumo { public Double nValPago; }
    }
}