package com.docs.git.service;

import com.docs.git.dto.GitCommitDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitLogService {

    /**
     * Pega commits NOVOS desde a última tag
     * Se não tiver tag, pega dos últimos 30 dias(do mês)
     */
    public List<GitCommitDTO> getGitLogsSince(String ultimaTag) {
        List<GitCommitDTO> commits = new ArrayList<>();

        try {
            // Pega qual tag usar (se não passou, busca a última)
            String tagParaUsar = ultimaTag;
            if (tagParaUsar == null || tagParaUsar.isEmpty()) {
                tagParaUsar = buscarUltimaTag();
            }

            ProcessBuilder processBuilder;
            if (tagParaUsar != null) {
                // Pega commits desde a tag
                processBuilder = new ProcessBuilder(
                    "git", "log", tagParaUsar + "..HEAD",
                    "--pretty=format:%H|%s|%cd", "--date=iso"
                );
            } else {
                // Não tem tag? Pega últimos 30 dias
                processBuilder = new ProcessBuilder(
                    "git", "log", "--since=30 days ago",
                    "--pretty=format:%H|%s|%cd", "--date=iso"
                );
            }
            
            // Executa e lê a saída
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] partes = line.split("\\|", 3);
                if (partes.length >= 2) {
                    commits.add(new GitCommitDTO(
                        partes[0],           // sha
                        partes[1],           // mensagem
                        null,               // type
                        partes.length >= 3 ? partes[2] : ""  // data
                    ));
                }
            }

            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar git log: " + e.getMessage(), e);
        }
        
        return commits;
    }

    /**
     * Busca a última tag do repositório
     * Retorna null se não encontrar
     */
    public String getLastTag() {
        return buscarUltimaTag();
    }

    private String buscarUltimaTag() {
        try {
            Process process = new ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String tag = reader.readLine();
            process.waitFor();
            
            if (process.exitValue() == 0 && tag != null && !tag.trim().isEmpty()) {
                return tag.trim();
            }
        } catch (Exception e) {
            // Ignora erro
        }
        return null;
    }
}
/*
Aqui e o coracao do projeto em minha opiniao, e aqui que realiza a coleta dos commits
do repositorio git local utilizando o log e assim alimentando a lista.
 */