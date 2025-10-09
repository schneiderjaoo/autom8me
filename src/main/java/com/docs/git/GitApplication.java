package com.docs.git;

import com.docs.git.dto.GitCommitDTO;
import com.docs.git.service.CommitService;
import com.docs.git.service.GeminiService;
import com.docs.git.service.GitLogService;
import com.docs.git.service.ReleaseNotesService;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class GitApplication {

    public static void main(String[] args) {
        String language = "pt-BR"; // "en-US", "es-ES"
        int[] version = {0, 2, 1};
        String tag = "v0.2.1";

        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        // captura os commits do git
        List<GitCommitDTO> commits = gitLogService.getGitLogs();

        commits = commitService.classifyCommits(commits);

        // Release Notes tradicional
        String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, version, tag, language);
        
        System.out.println(releaseNotesTemplate+"\n");

        String intelligentReleaseNotes = geminiService.generateResponse(releaseNotesTemplate);
        //System.out.println(intelligentReleaseNotes);
    }
}
