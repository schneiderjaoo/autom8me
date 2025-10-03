package com.docs.git.service;

import com.docs.git.dto.GitCommitDTO;
import com.docs.git.enums.CommitType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReleaseNotesService {

    public String generateReleaseNotes(List<GitCommitDTO> commits, String version, String tag) {
        String hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StringBuilder texto = new StringBuilder();

        // Cabeçalho
        texto.append("## Primeira \"Release\" Beta ").append(tag).append("\n");
        texto.append("**Tag:** ").append(tag).append("\n");
        texto.append("**Status:** Pre-release\n");
        texto.append("**Data:** ").append(hoje).append("\n");
        texto.append("### Destaques\n\n");

        // Funcionalidades (FEAT)
        texto.append("**Novas Funcionalidades:**\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.FEAT) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Correções (FIX)
        texto.append("**Correções:**\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.FIX) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Hotfixes (HOTFIX)
        texto.append("**Correções Críticas:**\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.HOTFIX) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Documentação (DOCS)
        texto.append("**Documentação:**\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.DOCS) {
                String mensagem = limparMensagem(commit.getMessage());
                if (!mensagem.contains("merge branch")) { // Pular merges
                    texto.append("- ").append(mensagem).append(".\n");
                }
            }
        }
        texto.append("\n");

        // Melhorias (REFACTOR)
        texto.append("**Melhorias:**\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.REFACTOR) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Frases motivacionais
        texto.append("> *É aqui que a automação começa* \n");

        return texto.toString();
    }

    // Método simples para limpar a mensagem
    private String limparMensagem(String mensagem) {
        // Remove "feat:", "fix:", etc.
        if (mensagem.toLowerCase().startsWith("feat:")) {
            mensagem = mensagem.substring(5).trim();
        }
        if (mensagem.toLowerCase().startsWith("fix:")) {
            mensagem = mensagem.substring(4).trim();
        }
        if (mensagem.toLowerCase().startsWith("hotfix:")) {
            mensagem = mensagem.substring(7).trim();
        }
        if (mensagem.toLowerCase().startsWith("docs:")) {
            mensagem = mensagem.substring(5).trim();
        }
        if (mensagem.toLowerCase().startsWith("refactor:")) {
            mensagem = mensagem.substring(9).trim();
        }

        // Deixa primeira letra maiúscula
        if (mensagem.length() > 0) {
            mensagem = Character.toUpperCase(mensagem.charAt(0)) + mensagem.substring(1);
        }

        // Remove ponto final se já tiver (para não ficar duplo)
        if (mensagem.endsWith(".")) {
            mensagem = mensagem.substring(0, mensagem.length() - 1);
        }

        return mensagem;
    }
}
/*
O service foi criado para gerar notas de release a partir de uma lista de commits classificados por tipo.
Ele formata as mensagens dos commits para criar destaques claros e concisos na release notes.

Primeira Release Notes Gerada manualmente:

## First "BETA" Release � `beta`

**Tag:** `beta`
**Status:** Pre-release
**Date:** `2025-04-15`

### Highlights

- Initial DevOps repository structure created with focus on scalability and learning.
- First CI/CD pipelines configured (Jenkins/GitHub Actions in progress).
- Docker and Terraform organized by environments (`dev`, `prod`).
- Automation scripts added for common operational tasks.
- First Prometheus alert rules (`basic-rules.yml`) implemented for observability.
- Project kicked off with best practices in versioning and Git tagging.

> _This is where automation begins_ ?
> _�Automate me� � sounds like number eight._
*/