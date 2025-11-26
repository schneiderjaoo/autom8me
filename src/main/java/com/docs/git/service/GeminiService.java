package com.docs.git.service;

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
            if (input != null) {
                props.load(input);
                geminiKey = props.getProperty("gemini.api.key");
                input.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar propriedades: " + e.getMessage());
        }
        
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            geminiKey = envKey;
        }
    }

    public String generateResponse(String prompt) {
        if (geminiKey == null || geminiKey.trim().isEmpty()) {
            return "API Key não configurada! Configure a variável de ambiente GEMINI_API_KEY ou o arquivo application.properties";
        }

        String promptEng = engPrompt(prompt, "pt-BR");

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON);

        // Usar query parameter (mais compat�vel)
        //String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiKey;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiKey;

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
            precisa estar evitando as mensagens gen�ricas como exemplo: ajustes, corre��es, melhorias, etc.
            Preciso que a release notes esteja em portugu�s, e que esteja dividida por t�picos como: 
            Novas Funcionalidades, Corre��es de Bugs, Melhorias, Atualiza��es e que cada t�pico tenha uma breve 
            descri��o do que foi feito, mas lembrando que isso vai ser disponibilizado para o cliente assim n�o 
            podendo ser algo muito t�cnico, tamb�m devemos lembrar que o FIX, FEATURE, as categorias que utilizamos 
            n�o v�o poder ser mantidas, j� que ser� disponibilizado para o usu�rio final devemos remover esses caras, 
            j� que pode causar deixar o usu�rio final confuso exemplo:
            - FEaT - Criado nova pipeline para validar todos os novos c�digos. deve virar
            - Criado nova pipeline para validar todos os novos c�digos.
            Exemplo:
            %s
            
            """, idioma, baseReleaseNotes);
    }
}
