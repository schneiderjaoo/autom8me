package com.docs.git.service;

import com.docs.git.dto.GitCommitDTO;
import com.docs.git.enums.CommitType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReleaseNotesService {

    public String generateReleaseNotes(List<GitCommitDTO> commits, int[] version, String tag, String idioma) {

        idioma = idioma.toLowerCase();

        String quinzenalLabel = idioma.equals("pt-br") ? "Quinzenal" :
                            idioma.equals("en-us") ? "Biweekly" : "Quincenal";
        String versaoLabel = idioma.equals("pt-br") ? "**Versão: **" :
                idioma.equals("en-us") ? "**Version: **" : "**Versión: **";
        String dataLabel = idioma.equals("pt-br") ? "**Data: **" :
                idioma.equals("en-us") ? "**Date: **" : "**Fecha: **";
        String formatoDataIdiomaLabel = idioma.equals("pt-BR") ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) :
                            idioma.equals("en-us") ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String destaquesLabel = idioma.equals("pt-br") ? "## Destaques" :
                            idioma.equals("en-us") ? "## Highlights" : "## Destacados";
        String novasFuncionalidadesLabel = idioma.equals("pt-br") ? "**Novas Funcionalidades:**" :
                            idioma.equals("en-us") ? "**New Features:**" : "**Nuevas Funcionalidades:**";
        String correcoesLabel = idioma.equals("pt-br") ? "**Correções:**" :
                            idioma.equals("en-us") ? "**Fixes:**" : "**Correcciones:**";
        String hotfixesLabel = idioma.equals("pt-br") ? "**Correções Críticas:**" :
                            idioma.equals("en-us") ? "**Critical Fixes:**" : "**Correcciones Críticas:**";
        String melhoriasLabel = idioma.equals("pt-br") ? "**Melhorias:**" :
                            idioma.equals("en-us") ? "**Improvements:**" : "**Mejoras:**";
        String docsLabel = idioma.equals("pt-br") ? "**Documentação:**" :
                idioma.equals("en-us") ? "**Documentation:**" : "**Documentación:**";
        String motivacionalLabel = idioma.equals("pt-br") ? "*É aqui que a automação começa*" :
                            idioma.equals("en-us") ? "*This is where automation begins*" : "*Aquí es donde comienza la automatización*";

        StringBuilder texto = new StringBuilder();

        // Cabeçalho
        texto.append("\n# Release Notes ").append(quinzenalLabel).append(" \n\n");
        texto.append(versaoLabel).append(tag).append("\n");
        texto.append(dataLabel).append(formatoDataIdiomaLabel).append("\n\n");
        texto.append(destaquesLabel).append("\n\n");

        // Funcionalidades (FEAT)
        texto.append(novasFuncionalidadesLabel).append("\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.FEAT) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Correções (FIX)
        texto.append(correcoesLabel).append("\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.FIX) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Hotfixes (HOTFIX)
        texto.append(hotfixesLabel).append("\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.HOTFIX) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Melhorias (REFACTOR)
        texto.append(melhoriasLabel).append("\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.REFACTOR) {
                String mensagem = limparMensagem(commit.getMessage());
                texto.append("- ").append(mensagem).append(".\n");
            }
        }
        texto.append("\n");

        // Documentação (DOCS)
        texto.append(docsLabel).append("\n");
        for (GitCommitDTO commit : commits) {
            if (commit.getType() == CommitType.DOCS) {
                String mensagem = limparMensagem(commit.getMessage());
                if (!mensagem.contains("merge branch")) { // Pular merges
                    texto.append("- ").append(mensagem).append(".\n");
                }
            }
        }
        texto.append("\n");

        // Frases motivacionais
        texto.append(motivacionalLabel).append("\n");

        return texto.toString();
    }

    // Método simples para limpar a mensagem
    private String limparMensagem(String mensagem) {
        // Remove "feat:", "fix:", etc.
        if (mensagem.toLowerCase().startsWith("feat - ")) {
            mensagem = mensagem.substring(7).trim();
        } else if (mensagem.toLowerCase().startsWith("fix - ")) {
            mensagem = mensagem.substring(6).trim();
        } else if (mensagem.toLowerCase().startsWith("bugfix - ")) {
            mensagem = mensagem.substring(9).trim();
        } else if (mensagem.toLowerCase().startsWith("hotfix - ")) {
            mensagem = mensagem.substring(9).trim();
        } else if (mensagem.toLowerCase().startsWith("docs - ")) {
            mensagem = mensagem.substring(7).trim();
        } else if (mensagem.toLowerCase().startsWith("refactor - ")) {
            mensagem = mensagem.substring(11).trim();
        } else if (mensagem.toLowerCase().startsWith("merge")) {
            mensagem = mensagem.substring(5).trim();
        } else if (mensagem.toLowerCase().startsWith("bugfix")) {
            mensagem = mensagem.substring(6).trim();
        } else if (mensagem.toLowerCase().startsWith("feat/bugfix")) {
            mensagem = mensagem.substring(11).trim();
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