package com.docs.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.lang.ProcessBuilder;
import java.util.List;

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
import org.springframework.core.env.Environment;

/*
* @author schneiderjaoo
* @version olha a ultima tag do repositório git
*/

@SpringBootApplication
public class GitApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GitApplication.class, args);
        ReleaseNotesRepository releaseNotesRepository = context.getBean(ReleaseNotesRepository.class);
        Environment env = context.getEnvironment();

        String language = "pt-BR";
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        // Lê configurações do application.properties
        String repoPath = env.getProperty("app.repo.booksys.path", "/Users/user/Documents/GitClones/BookSys");
        String relativeDir = env.getProperty("app.repo.booksys.release-notes-dir", "books/release-notes/Diário de Mudanças");
        String targetRepoUrl = env.getProperty("app.repo.booksys.url", "https://github.com/schneiderjaoo/bookSys.git");
        String originalUrl = null;

        try {
            /*
            * Pega a última tag do repositório
            * Analisa a tag para extrair a versão sem o "v" inicial
            * Se não houver tag, começa em 0.0.0
            */
            String lastTag = gitLogService.getLastTag();
            Version version;
            if (lastTag != null && !lastTag.isBlank()) {
                String cleanTag = lastTag.trim();
                if (cleanTag.startsWith("v") || cleanTag.startsWith("V")) {
                    cleanTag = cleanTag.substring(1);
                }
                String[] parts = cleanTag.split("\\.");
                int major = parts.length > 0 ? parseNumber(parts[0]) : 0;
                int minor = parts.length > 1 ? parseNumber(parts[1]) : 0;
                int patch = parts.length > 2 ? parseNumber(parts[2]) : 0;
                version = new Version(major, minor, patch);
            } else {
                // Caso nao ache nenhuma tag, inicia em 0.0.0
                version = new Version(0, 0, 0);
            }

            List<GitCommitDTO> commits = gitLogService.getGitLogsSince(lastTag);

            if (commits.isEmpty()) {
                System.out.println("Sem commits versao vai ser v0.0.1");
                version.addPatch();
            }
            // Classifica os commits
            commits = commitService.classifyCommits(commits);

            /*
            * SemVer rules:
            * Major: refactor, reaf
            * Minor: feat
            * Patch: fix
            * Só vai gerar versão se encontrar algum desses tipos de commit
            */
            if (!commits.isEmpty()) {
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
            }

            String tag = "v" + version.toString();

            ProcessBuilder pbTag =
                    new ProcessBuilder("git", "tag", "-f", tag);
            Process processTag = pbTag.start();
            int exitCodeTag = processTag.waitFor();
            if (exitCodeTag != 0) {
                String stderr = readStream(processTag.getErrorStream());
                throw new RuntimeException("Erro ao criar tag local: " + stderr);
            }

            ProcessBuilder pbPush =
                    new ProcessBuilder("git", "push", "origin", tag, "--force");
            Process processPush = pbPush.start();
            int exitCodePush = processPush.waitFor();
            if (exitCodePush != 0) {
                String stderr = readStream(processPush.getErrorStream());
                throw new RuntimeException("Erro ao enviar tag para o repositório remoto: " + stderr);
            }

            int[] versionArray = version.toArray();
            String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, versionArray, tag, language);
            String intelligentReleaseNotes = geminiService.generateResponse(releaseNotesTemplate);

            ReleaseNotes releaseNotesDocument = new ReleaseNotes(
                    tag,
                    version.toString(),
                    language,
                    releaseNotesTemplate,
                    intelligentReleaseNotes
            );

            releaseNotesRepository.save(releaseNotesDocument);

            originalUrl = getCurrentRemoteUrl(repoPath);
            if (originalUrl == null || originalUrl.isBlank()) {
                throw new IllegalStateException("Could not determine original remote URL for repository at " + repoPath);
            }

            System.out.println("Switching remote to target repository " + targetRepoUrl);
            setRemoteUrl(repoPath, targetRepoUrl);

            runCommand(repoPath, "git", "fetch", "origin");
            runCommand(repoPath, "git", "checkout", "main");
            
            // Limpar working tree antes do pull
            try {
                runCommand(repoPath, "git", "reset", "--hard", "HEAD");
            } catch (Exception e) {
                // Ignora se não houver nada para resetar
            }
            try {
                runCommand(repoPath, "git", "clean", "-fd");
            } catch (Exception e) {
                // Ignora se não houver nada para limpar
            }
            
            runCommand(repoPath, "git", "pull", "--rebase", "origin", "main");

            // Criar arquivos DEPOIS do pull para não serem removidos pelo reset
            File baseDir = new File(new File(repoPath), relativeDir);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            String releaseFileName = tag + ".md";
            String intelligentFileName = "Ge " + tag + ".md";

            File releaseFile = new File(baseDir, releaseFileName);
            writeFile(releaseFile, releaseNotesTemplate);

            File intelligentFile = new File(baseDir, intelligentFileName);
            writeFile(intelligentFile, intelligentReleaseNotes);

            String releaseRelativePath = relativeDir + "/" + releaseFileName;
            String intelligentRelativePath = relativeDir + "/" + intelligentFileName;

            // Para adicionar os arquivos de release notes no outro repositorio
            runCommand(repoPath, "git", "add", releaseRelativePath, intelligentRelativePath);

            commitChanges(repoPath, "update diário de mudanças " + tag);
            runCommand(repoPath, "git", "push", "origin", "HEAD:main");

        } catch (Exception e){
            e.printStackTrace();
        } finally{
            if (originalUrl != null && !originalUrl.isBlank()) {
                try {
                    System.out.println("Voltando o remote " + originalUrl);
                    setRemoteUrl(repoPath, originalUrl);
                } catch (Exception restoreException) {
                    restoreException.printStackTrace();
                }
            }

            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
        }

    }

    private static void runCommand(String repoPath, String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(repoPath));
        Process p = pb.start();
        String stdout = readStream(p.getInputStream());
        String stderr = readStream(p.getErrorStream());
        int exit = p.waitFor();
        if (exit != 0) {
            String message = "Erro ao executar comando: " + String.join(" ", command);
            if (!stdout.isBlank()) {
                message += System.lineSeparator() + "STDOUT: " + stdout;
            }
            if (!stderr.isBlank()) {
                message += System.lineSeparator() + "STDERR: " + stderr;
            }
            throw new RuntimeException(message);
        }
    }

    private static void writeFile(File file, String content) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    private static String getCurrentRemoteUrl(String repoPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("git", "remote", "get-url", "origin");
        pb.directory(new File(repoPath));
        Process p = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String url = reader.readLine();
            int exit = p.waitFor();
            if (exit != 0) {
                String error = readStream(p.getErrorStream());
                throw new RuntimeException("Erro ao obter remote origin: " + error);
            }
            return url;
        }
    }

    private static void setRemoteUrl(String repoPath, String newUrl) throws Exception {
        runCommand(repoPath, "git", "remote", "set-url", "origin", newUrl);
    }

    private static void commitChanges(String repoPath, String message) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("git", "commit", "-m", message);
        pb.directory(new File(repoPath));
        Process p = pb.start();
        String stdout = readStream(p.getInputStream());
        String stderr = readStream(p.getErrorStream());
        int exit = p.waitFor();

        if (exit == 0) {
            return;
        }

        String combinedOutput = (stdout + " " + stderr).toLowerCase();
        if (combinedOutput.contains("nothing to commit")) {
            // Força commit mesmo sem mudanças
            System.out.println("Nenhuma mudança detectada. Criando commit vazio...");
            ProcessBuilder pbEmpty = new ProcessBuilder("git", "commit", "--allow-empty", "-m", message);
            pbEmpty.directory(new File(repoPath));
            Process pEmpty = pbEmpty.start();
            String stdoutEmpty = readStream(pEmpty.getInputStream());
            String stderrEmpty = readStream(pEmpty.getErrorStream());
            int exitEmpty = pEmpty.waitFor();
            
            if (exitEmpty != 0) {
                StringBuilder messageBuilder = new StringBuilder("Erro ao executar git commit --allow-empty");
                if (!stdoutEmpty.isBlank()) {
                    messageBuilder.append(System.lineSeparator()).append("STDOUT: ").append(stdoutEmpty);
                }
                if (!stderrEmpty.isBlank()) {
                    messageBuilder.append(System.lineSeparator()).append("STDERR: ").append(stderrEmpty);
                }
                throw new RuntimeException(messageBuilder.toString());
            }
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("Erro ao executar git commit");
        if (!stdout.isBlank()) {
            messageBuilder.append(System.lineSeparator()).append("STDOUT: ").append(stdout);
        }
        if (!stderr.isBlank()) {
            messageBuilder.append(System.lineSeparator()).append("STDERR: ").append(stderr);
        }
        throw new RuntimeException(messageBuilder.toString());
    }

    private static int parseNumber(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String readStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator());
                }
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
