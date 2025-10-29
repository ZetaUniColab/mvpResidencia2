package com.easy.chatbot.controller;

import com.easy.chatbot.service.OmieIntegrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/omie")
public class OmieController {

    private final OmieIntegrationService omieService;

    public OmieController(OmieIntegrationService omieService) {
        this.omieService = omieService;
    }

    @GetMapping("/movimentos")
    public Mono<String> listarMovimentos() {
        return omieService.listarMovimentos();
    }

    @GetMapping("/categorias")
    public Mono<String> listarCategorias() {
        return omieService.listarCategorias();
    }

    @GetMapping("/ambas")
    public Mono<Map<String, String>> listarAmbas() {
        return omieService.listarAmbas();
    }
}
