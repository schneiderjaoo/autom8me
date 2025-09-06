package com.docs.git.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiKey;

    public String generateResponse(String prompt) {
        String promptEng = engPrompt(prompt);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);

        String urlWithKey = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + geminiKey;

        String requestBody = """
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                ]
            }
        """.formatted(promptEng);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, head);

        try {
            return restTemplate.postForObject(urlWithKey, entity, String.class);
        } catch (Exception e) {
            return "Erro ao chamar a API: " + e.getMessage();
        }
    }

    private String engPrompt(String prompt) {
        return """
            Gere uma release notes detalhada a partir das mensagens de commits que vou estar mandando a baixo,
            precisa estar evitando as mensagens genéricas como exemplo: ajustes, correções, melhorias, etc.
            Preciso que a release notes esteja em português, e que esteja dividida por tópicos como: 
            Novas Funcionalidades, Correções de Bugs, Melhorias, Atualizações e que cada tópico tenha uma breve 
            descrição do que foi feito, mas lembrando que isso vai ser disponibilizado para o cliente assim não 
            podendo ser algo muito técnico.
            Exemplo a baixo: 
            
            """ + prompt;
    }
}