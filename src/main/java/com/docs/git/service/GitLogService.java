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
     * Busca commits desde a última tag
     * 
     * Se não tiver tag OU não tiver commits desde a tag:
     * - Se daysFallback = 0: busca TODOS os commits
     * - Se daysFallback > 0: busca commits dos últimos X dias
     */
    public List<GitCommitDTO> getGitLogsSince(String ultimaTag, int daysFallback) {
        List<GitCommitDTO> commits = new ArrayList<>();
        boolean rodandoLocal = false;

        try {
            // Pega qual tag usar (se não passou, busca a última)
            String tagParaUsar = ultimaTag;
            if (tagParaUsar == null || tagParaUsar.isEmpty()) {
                tagParaUsar = buscarUltimaTag();
            }

            ProcessBuilder processBuilder;
            if (tagParaUsar != null && !rodandoLocal) {
                // Pega commits desde a tag
                processBuilder = new ProcessBuilder(
                    "git", "log", tagParaUsar + "..HEAD",
                    "--pretty=format:%H|%s|%cd", "--date=iso"
                );
            } else {
                // Não tem tag? Busca todos ou pelos dias configurados
                if (daysFallback <= 0 || rodandoLocal) {
                    // daysFallback = 0 significa buscar TODOS os commits
                    processBuilder = new ProcessBuilder(
                        "git", "log",
                        "--pretty=format:%H|%s|%cd", "--date=iso"
                    );
                } else {
                    // Busca commits dos últimos X dias
                    processBuilder = new ProcessBuilder(
                        "git", "log", "--since=" + daysFallback + " days ago",
                        "--pretty=format:%H|%s|%cd", "--date=iso"
                    );
                }
            }
            
            // Executa o comando Git e lê os commits
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
                        partes[0],           // SHA do commit
                        partes[1],           // Mensagem do commit
                        null,                // Tipo será classificado depois
                        partes.length >= 3 ? partes[2] : ""  // Data do commit
                    ));
                }
            }

            process.waitFor();
            
            // Se não encontrou commits desde a tag, busca todos ou pelos dias configurados
            if (commits.isEmpty() && tagParaUsar != null) {
                if (daysFallback <= 0) {
                    // Busca TODOS os commits
                    processBuilder = new ProcessBuilder(
                        "git", "log",
                        "--pretty=format:%H|%s|%cd", "--date=iso"
                    );
                } else {
                    // Busca pelos dias configurados
                    processBuilder = new ProcessBuilder(
                        "git", "log", "--since=" + daysFallback + " days ago",
                        "--pretty=format:%H|%s|%cd", "--date=iso"
                    );
                }
                
                Process processFallback = processBuilder.start();
                BufferedReader readerFallback = new BufferedReader(
                    new InputStreamReader(processFallback.getInputStream())
                );
                
                String lineFallback;
                while ((lineFallback = readerFallback.readLine()) != null) {
                    if (lineFallback.trim().isEmpty()) continue;
                    
                    String[] partes = lineFallback.split("\\|", 3);
                    if (partes.length >= 2) {
                        commits.add(new GitCommitDTO(
                            partes[0],
                            partes[1],
                            null,
                            partes.length >= 3 ? partes[2] : ""
                        ));
                    }
                }
                
                processFallback.waitFor();
            }
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