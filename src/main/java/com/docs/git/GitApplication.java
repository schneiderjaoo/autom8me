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

@SpringBootApplication
public class GitApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GitApplication.class, args);
        ReleaseNotesRepository releaseNotesRepository = context.getBean(ReleaseNotesRepository.class);

        String language = "pt-BR";
        int major = 0;
        int minor = 0;
        int patch = 0;

        Version version = new Version(major, minor, patch);
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        String repoPath = "/Users/user/Documents/GitClones/BookSys";
        String relativeDir = "books/release-notes/Diário de Mudanças";
        String targetRepoUrl = "https://github.com/schneiderjaoo/bookSys.git";
        String originalUrl = null;

        try {
            System.out.println("Collecting repository changes to generate release notes...");
            List<GitCommitDTO> commits = gitLogService.getGitLogsSince(null);

            if (commits.isEmpty()) {
                System.out.println("No commits found since the last tag. Skipping release notes generation.");
                return;
            }

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
                    new ProcessBuilder("git", "push", "origin", tag);
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

            File baseDir = new File(new File(repoPath), relativeDir);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            File releaseFile = new File(baseDir, "diario-mudancas.md");
            writeFile(releaseFile, releaseNotesTemplate);

            File intelligentFile = new File(baseDir, "diario-inteligente.md");
            writeFile(intelligentFile, intelligentReleaseNotes);

            System.out.println("Switching remote to target repository " + targetRepoUrl);
            setRemoteUrl(repoPath, targetRepoUrl);

            runCommand(repoPath, "git", "fetch", "origin");
            runCommand(repoPath, "git", "checkout", "main");
            runCommand(repoPath, "git", "pull", "--rebase", "origin", "main");

            System.out.println("Committing release notes to target repository...");
            runCommand(repoPath, "git", "add", ".");
            runCommand(repoPath, "git", "commit", "-m", "chore: update diário de mudanças " + tag);
            runCommand(repoPath, "git", "tag", "-f", tag);
            runCommand(repoPath, "git", "push", "origin", "HEAD:main", "--force");
            runCommand(repoPath, "git", "push", "origin", tag, "--force");

        } catch (Exception e){
            e.printStackTrace();
        } finally{
            if (originalUrl != null && !originalUrl.isBlank()) {
                try {
                    System.out.println("Restoring original remote URL " + originalUrl);
                    setRemoteUrl("/Users/user/Documents/GitClones/BookSys", originalUrl);
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
