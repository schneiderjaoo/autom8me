package com.docs.git.service;

import com.docs.git.dto.GitCommitDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitLogService {

    public List<GitCommitDTO> getGitLogs() {
        List<GitCommitDTO> commits = new ArrayList<>();

        try {
            // exec git log a onde ele esta
            // git log --pretty=format:"%H|%s|%cd" --date=iso
            Process process = new ProcessBuilder("git", "log",
                    "--pretty=format:%H|%s|%cd", "--date=iso"
            ).start();

            // pega a saida do console
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            // le linha por linha e separa por |
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    String sha = parts[0];
                    String message = parts[1];
                    String date = parts[2];

                    commits.add(new GitCommitDTO(sha, message, null, date));
                }
            }

            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar git log: " + e.getMessage(), e);
        }
        return commits;
    }
}
/*
Aqui e o coracao do projeto em minha opiniao, e aqui que realiza a coleta dos commits
do repositorio git local utilizando o log e assim alimentando a lista.
 */