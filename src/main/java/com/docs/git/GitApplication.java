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

        // Idioma das release notes
        String language = "pt-BR";
        
        // Services
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        // Lê configurações do application.properties
        String repoPath = env.getProperty("app.repo.booksys.path", "/Users/user/Documents/GitClones/BookSys");
        String relativeDir = env.getProperty("app.repo.booksys.release-notes-dir", "books/release-notes/Diário de Mudanças");
        String targetRepoUrl = env.getProperty("app.repo.booksys.url", "https://github.com/schneiderjaoo/bookSys.git");
        int daysFallback = Integer.parseInt(env.getProperty("app.release-notes.days-fallback", "60"));
        String originalUrl = null;

        try {
            // Garante que as tags remotas estão disponíveis
            ProcessBuilder pbFetchTags = new ProcessBuilder("git", "fetch", "--tags", "origin");
            Process processFetchTags = pbFetchTags.start();
            processFetchTags.waitFor();
            
            /*
            * Pega a última tag do repositório
            * Analisa a tag para extrair a versão sem o "v" inicial
            * Se não houver tag, começa em 0.0.0
            */
            String lastTag = gitLogService.getLastTag();
            
            // Calcula a versão atual baseada na última tag
            Version version = calcularVersaoAtual(lastTag);
            System.out.println("Versão atual base: " + version.toString());

            // Busca os commits desde a última tag (ou todos se não tiver tag)
            System.out.println("Buscando commits...");
            List<GitCommitDTO> commits = gitLogService.getGitLogsSince(lastTag, daysFallback);
            System.out.println("Commits encontrados: " + commits.size());

            if (commits.isEmpty()) {
                version.addPatch();
            }
            // Classifica os commits
            commits = commitService.classifyCommits(commits);

            // Regras SemVer:
            // - refactor/reaf = incrementa MAJOR (mudança grande)
            // - feat = incrementa MINOR (nova funcionalidade)
            // - fix = incrementa PATCH (correção)
            calcularNovaVersao(commits, version);

            // Cria a tag com a nova versão
            String tag = "v" + version.toString();
            System.out.println("Gerando release: " + tag);

            // Cria a tag no repositório local
            System.out.println("Criando tag local: " + tag);
            criarTagLocal(tag);

            // Envia a tag para o repositório remoto
            System.out.println("Enviando tag para o GitHub...");
            enviarTagParaRemoto(tag);

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

            try {
                releaseNotesRepository.save(releaseNotesDocument);
            } catch (Exception e) {
                System.out.println("Aviso: Erro ao salvar no MongoDB: " + e.getMessage());
            }

            originalUrl = getCurrentRemoteUrl(repoPath);
            if (originalUrl == null || originalUrl.isBlank()) {
                throw new IllegalStateException("Could not determine original remote URL for repository at " + repoPath);
            }

            String currentRemote = originalUrl.toLowerCase();
            boolean alreadyPointsToBookSys = currentRemote.contains("schneiderjaoo") && 
                                            currentRemote.contains("booksys");
            
            if (!alreadyPointsToBookSys) {
                setRemoteUrl(repoPath, targetRepoUrl);
            }

            // Valida se o repositório BookSys existe
            validarRepositorioBookSys(repoPath);
            
            // PASSO 15: Atualiza o repositório BookSys (fetch, checkout, pull)
            String branchName = "main";
            atualizarRepositorioBookSys(repoPath, branchName);

            // PASSO 16: Cria os arquivos de release notes no BookSys
            System.out.println("Criando arquivos de release notes...");
            File releaseFile = criarArquivosReleaseNotes(repoPath, relativeDir, tag, releaseNotesTemplate, intelligentReleaseNotes);

            // PASSO 17: Faz commit e push dos arquivos no BookSys
            System.out.println("Fazendo commit no BookSys...");
            fazerCommitNoBookSys(repoPath, relativeDir, tag, releaseFile, branchName);
            
            System.out.println("Release notes commitados com sucesso: " + tag);

        } catch (Exception e){
            System.err.println("Erro durante execução: " + e.getMessage());
            e.printStackTrace();
        } finally{
            if (originalUrl != null && !originalUrl.isBlank()) {
                setRemoteUrl(repoPath, originalUrl);
            }

            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
        }

    }
    
    /**
     * Calcula a versão atual baseada na última tag encontrada
     * Se não tiver tag, começa em 0.0.0
     */
    private static Version calcularVersaoAtual(String lastTag) {
        if (lastTag != null && !lastTag.isBlank()) {
            // Remove o "v" da frente da tag (ex: "v1.2.3" vira "1.2.3")
            String cleanTag = lastTag.trim();
            if (cleanTag.startsWith("v") || cleanTag.startsWith("V")) {
                cleanTag = cleanTag.substring(1);
            }
            
            // Separa a versão em partes (ex: "1.2.3" vira ["1", "2", "3"])
            String[] parts = cleanTag.split("\\.");
            int major = parts.length > 0 ? parseNumber(parts[0]) : 0;
            int minor = parts.length > 1 ? parseNumber(parts[1]) : 0;
            int patch = parts.length > 2 ? parseNumber(parts[2]) : 0;
            
            return new Version(major, minor, patch);
        } else {
            // Se não tem tag, começa do zero
            return new Version(0, 0, 0);
        }
    }
    
    /**
     * Calcula a nova versão baseada nos tipos de commit encontrados
     */
    private static void calcularNovaVersao(List<GitCommitDTO> commits, Version version) {
        if (commits.isEmpty()) {
            return;
        }
        
        for (GitCommitDTO commit : commits) {
            String mensagemCommit = commit.getMessage().toLowerCase();

            // refactor ou reaf = mudança grande (MAJOR)
            if (mensagemCommit.startsWith("refactor") || mensagemCommit.startsWith("reaf")) {
                version.addMajor();
            } 
            // feat = nova funcionalidade (MINOR)
            else if (mensagemCommit.startsWith("feat")) {
                version.addMinor();
            } 
            // fix = correção de bug (PATCH)
            else if (mensagemCommit.startsWith("fix")) {
                version.addPatch();
            }
        }
    }
    
    /**
     * Cria a tag no repositório local
     */
    private static void criarTagLocal(String tag) throws Exception {
        ProcessBuilder pbTag = new ProcessBuilder("git", "tag", "-f", tag);
        Process processTag = pbTag.start();
        int exitCodeTag = processTag.waitFor();
        
        if (exitCodeTag != 0) {
            String stderr = readStream(processTag.getErrorStream());
            throw new RuntimeException("Erro ao criar tag local: " + stderr);
        }
    }
    
    /**
     * Envia a tag para o repositório remoto (GitHub)
     */
    private static void enviarTagParaRemoto(String tag) throws Exception {
        ProcessBuilder pbPush = new ProcessBuilder("git", "push", "origin", tag, "--force");
        Process processPush = pbPush.start();
        int exitCodePush = processPush.waitFor();
        
        if (exitCodePush != 0) {
            String stderr = readStream(processPush.getErrorStream());
            throw new RuntimeException("Erro ao enviar tag para o repositório remoto: " + stderr);
        }
    }
    
    /**
     * Valida se o repositório BookSys existe e é válido
     */
    private static void validarRepositorioBookSys(String repoPath) {
        File repoDir = new File(repoPath);
        if (!repoDir.exists()) {
            throw new IllegalStateException("Repositório BookSys não encontrado em: " + repoPath);
        }
        if (!repoDir.isDirectory()) {
            throw new IllegalStateException("Caminho não é um diretório: " + repoPath);
        }
        
        File gitDir = new File(repoDir, ".git");
        if (!gitDir.exists()) {
            throw new IllegalStateException("Não é um repositório Git válido: " + repoPath);
        }
    }
    
    /**
     * Atualiza o repositório BookSys (busca mudanças, limpa e atualiza)
     */
    private static void atualizarRepositorioBookSys(String repoPath, String branchName) throws Exception {
        runCommand(repoPath, "git", "fetch", "origin");
        runCommand(repoPath, "git", "checkout", branchName);
        runCommand(repoPath, "git", "reset", "--hard", "HEAD");
        runCommand(repoPath, "git", "clean", "-fd");
        runCommand(repoPath, "git", "pull", "--rebase", "origin", branchName);
    }
    
    /**
     * Cria os arquivos de release notes no repositório BookSys
     */
    private static File criarArquivosReleaseNotes(String repoPath, String relativeDir, 
                                                   String tag, String releaseNotesTemplate, 
                                                   String intelligentReleaseNotes) throws IOException {
        File baseDir = new File(new File(repoPath), relativeDir);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created && !baseDir.exists()) {
                throw new IOException("Não foi possível criar o diretório: " + baseDir.getAbsolutePath());
            }
        }

        String releaseFileName = tag + ".md";
        String intelligentFileName = "Ge " + tag + ".md";

        File releaseFile = new File(baseDir, releaseFileName);
        writeFile(releaseFile, releaseNotesTemplate);

        File intelligentFile = new File(baseDir, intelligentFileName);
        writeFile(intelligentFile, intelligentReleaseNotes);

        if (!releaseFile.exists()) {
            throw new IOException("Arquivo não encontrado: " + releaseFile.getAbsolutePath());
        }
        if (!intelligentFile.exists()) {
            throw new IOException("Arquivo não encontrado: " + intelligentFile.getAbsolutePath());
        }
        
        return releaseFile;
    }
    
    /**
     * Faz commit e push dos arquivos de release notes no BookSys
     */
    private static void fazerCommitNoBookSys(String repoPath, String relativeDir, 
                                             String tag, File releaseFile, String branchName) throws Exception {
        String releaseFileName = tag + ".md";
        String intelligentFileName = "Ge " + tag + ".md";
        String releaseRelativePath = relativeDir + "/" + releaseFileName;
        String intelligentRelativePath = relativeDir + "/" + intelligentFileName;
        
        runCommand(repoPath, "git", "add", releaseRelativePath, intelligentRelativePath);
        commitChanges(repoPath, "update diário de mudanças " + tag);
        runCommand(repoPath, "git", "push", "origin", "HEAD:" + branchName);
    }
    
    /**
     * Executa um comando Git no repositório especificado
     */
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

    private static void setRemoteUrl(String repoPath, String newUrl) {
        try {
            runCommand(repoPath, "git", "remote", "set-url", "origin", newUrl);
        } catch (Exception e) {
            System.err.println("Aviso: Erro ao restaurar remote: " + e.getMessage());
        }
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
