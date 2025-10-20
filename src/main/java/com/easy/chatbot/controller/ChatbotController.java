package com.easy.chatbot.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatbotController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World from Java Spring Boot!";
    }
}