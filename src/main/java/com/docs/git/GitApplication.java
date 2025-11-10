package com.docs.git;

import java.util.Arrays;
import java.lang.ProcessBuilder;

import com.docs.git.model.Version;
import com.docs.git.model.ReleaseNotes;
import com.docs.git.dto.GitCommitDTO;
import com.docs.git.service.CommitService;
import com.docs.git.service.GeminiService;
import com.docs.git.service.GitLogService;
import com.docs.git.service.ReleaseNotesService;
import com.docs.git.repository.ReleaseNotesRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class GitApplication {

    public static void main(String[] args) {
        // Inicializa o contexto Spring para ter acesso ao MongoDB
        ApplicationContext context = SpringApplication.run(GitApplication.class, args);
        ReleaseNotesRepository releaseNotesRepository = context.getBean(ReleaseNotesRepository.class);
        
        String language = "pt-BR"; // "en-US", "es-ES"
        int major = 0;
        int minor = 0;
        int patch = 0;

        Version version = new Version(major, minor, patch);
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        try {
            // captura os commits novos desde a última tag
            List<GitCommitDTO> commits = gitLogService.getGitLogsSince(null);
            
            if (commits.isEmpty()) {
                System.out.println("Nenhum commit novo encontrado desde a última tag. Nada para processar.");
                return;
            }
            
            System.out.println("Encontrados " + commits.size() + " commit(s) novo(s) para processar.");

            commits = commitService.classifyCommits(commits);

            for (GitCommitDTO commit : commits) {
                String msg = commit.getMessage().toLowerCase();

                if (msg.startsWith("refactor") || msg.startsWith("reaf")) {
                    version.addMajor();
                } else if (msg.startsWith("feat")) {
                    version.addMinor();
                } else if (msg.startsWith("fix")) {
                    version.addPatch();
                }
            }

            String tag = "v" + version.toString();//"v0.2.1";

            /*
             * Está forçando pois se a tag existir vai apenas enviar a existente
             * gerando o release notes e atualizando o commit no GitHub.
             */
            ProcessBuilder pbTag =
                    new ProcessBuilder("git", "tag", "-f", tag);
            Process processTag = pbTag.start();
            int exitCodeTag = processTag.waitFor();
            if (exitCodeTag == 0) {
                ProcessBuilder pbPush =
                        new ProcessBuilder("git", "push", "origin", tag);
                Process processPush = pbPush.start();
                int exitCodePush = processPush.waitFor();
            }

            int[] versionArray = version.toArray();
            System.out.println("Versao: " + Arrays.toString(versionArray) + " - Tag: " + tag);
            // Release Notes tradicional
            String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, versionArray, tag, language);

            //System.out.println(releaseNotesTemplate+"\n");

            String intelligentReleaseNotes = geminiService.generateResponse(releaseNotesTemplate);
            //System.out.println(intelligentReleaseNotes);
            
            // Salva no MongoDB
            ReleaseNotes releaseNotesDocument = new ReleaseNotes(
                tag, 
                version.toString(), 
                language, 
                releaseNotesTemplate, // Release técnica "tradicional"
                intelligentReleaseNotes // Release inteligente
            );
            releaseNotesRepository.save(releaseNotesDocument);
            System.out.println("Release notes salvo no MongoDB com sucesso! ID: " + releaseNotesDocument.getId());
            
        } catch (Exception e){
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally{ 
            // Adiciona o fechamento do contexto Spring para o jar não ficar rodando infinitamente
            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
        }

    }
}
