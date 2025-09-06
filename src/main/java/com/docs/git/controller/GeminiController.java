package com.docs.git.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docs.git.service.GeminiService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/check")
    public String healthCheck() {
        return "GitDocs gemini ativo!";
    }

    @GetMapping("/generate")
    public String generateReleaseNotes(@RequestParam String prompt) {

        String response = geminiService.generateResponse(prompt);

        return response;
    }
}