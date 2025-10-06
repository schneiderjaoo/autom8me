package com.docs.git.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiService {

    private String geminiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiService() {
        // Carregar propriedades manualmente
        loadProperties();
    }

    private void loadProperties() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(input);
            geminiKey = props.getProperty("gemini.api.key");
        } catch (IOException e) {
            System.out.println("Erro ao carregar propriedades: " + e.getMessage());
        }
    }

    public String generateResponse(String prompt) {
        if (geminiKey == null || geminiKey.trim().isEmpty()) {
            return "API Key não configurada! Verifique o arquivo application.properties";
        }

        String promptEng = engPrompt(prompt, "pt-BR");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);

        // Usar query parameter (mais compatível)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiKey;

        // Construir JSON manualmente para evitar problemas de escape
        String requestBody = "{\n" +
            "  \"contents\": [\n" +
            "    {\n" +
            "      \"parts\": [\n" +
            "        {\n" +
            "          \"text\": \"" + escapeJsonString(promptEng) + "\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, head);

        try {
            String response = restTemplate.postForObject(url, entity, String.class);
            
            // Extrair apenas o texto da resposta JSON
            String extractedText = extractTextFromResponse(response);
            
            return extractedText;
        } catch (Exception e) {
            return "Erro ao chamar a API: " + e.getMessage();
        }
    }

    /**
     * Escapa caracteres especiais para JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        return input
            .replace("\\", "\\\\")  // Escape backslashes first
            .replace("\"", "\\\"")  // Escape double quotes
            .replace("\n", "\\n")   // Escape newlines
            .replace("\r", "\\r")   // Escape carriage returns
            .replace("\t", "\\t");  // Escape tabs
    }

    /**
     * Extrai apenas o texto da resposta JSON do Gemini
     */
    private String extractTextFromResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            
            // Navegar pela estrutura JSON: candidates[0].content.parts[0].text
            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode firstCandidate = candidatesNode.get(0);
                JsonNode contentNode = firstCandidate.path("content");
                JsonNode partsNode = contentNode.path("parts");
                
                if (partsNode.isArray() && partsNode.size() > 0) {
                    JsonNode firstPart = partsNode.get(0);
                    String text = firstPart.path("text").asText();
                    
                    if (text != null && !text.trim().isEmpty()) {
                        return text.trim();
                    }
                }
            }
            return response;
            
        } catch (Exception e) {
            System.out.println("Erro ao processar resposta JSON: " + e.getMessage());
            return response; // Fallback para resposta original
        }
    }

    private String engPrompt(String baseReleaseNotes, String idioma) {
        return String.format("""
            Gere uma release notes detalhada em %s a partir da release notes que vou estar enviando a baixo,
            precisa estar evitando as mensagens genéricas como exemplo: ajustes, correções, melhorias, etc.
            Preciso que a release notes esteja em português, e que esteja dividida por tópicos como: 
            Novas Funcionalidades, Correções de Bugs, Melhorias, Atualizações e que cada tópico tenha uma breve 
            descrição do que foi feito, mas lembrando que isso vai ser disponibilizado para o cliente assim não 
            podendo ser algo muito técnico, também devemos lembrar que o FIX, FEATURE, as categorias que utilizamos 
            não vão poder ser mantidas, já que será disponibilizado para o usuário final devemos remover esses caras, 
            já que pode causar deixar o usuário final confuso exemplo:
            - FEaT - Criado nova pipeline para validar todos os novos códigos. deve virar
            - Criado nova pipeline para validar todos os novos códigos.
            Exemplo:
            %s
            
            """, idioma, baseReleaseNotes);
    }
}
