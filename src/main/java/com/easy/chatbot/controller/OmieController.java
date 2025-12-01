package com.easy.chatbot.controller;

import com.easy.chatbot.service.OmieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controlador auxiliar para exposição direta dos dados da Omie.
 * Utilizado primariamente para debug e verificação dos dados brutos da API financeira.
 */
@RestController
@RequestMapping("/omie")
@Tag(name = "Integração Omie", description = "Endpoints diretos para consultar dados financeiros (Legado/Debug)")
public class OmieController {

    private final OmieService omieService;

    public OmieController(OmieService omieService) {
        this.omieService = omieService;
    }

    @GetMapping("/movimentos")
    @Operation(summary = "Listar Movimentos Financeiros", description = "Retorna a lista bruta de contas a pagar/receber direto da API da Omie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados recuperados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na comunicação com a Omie")
    })
    public Mono<String> listarMovimentos() {
        return omieService.listarMovimentos();
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar Categorias (DRE)", description = "Retorna a árvore de categorias cadastrada na Omie para classificação financeira.")
    public Mono<String> listarCategorias() {
        return omieService.listarCategorias();
    }

    @GetMapping("/ambas")
    @Operation(summary = "Dados Completos", description = "Traz tanto movimentos quanto categorias em uma única chamada (Agregado).")
    public Mono<Map<String, String>> listarAmbas() {
        return omieService.listarAmbas();
    }
}