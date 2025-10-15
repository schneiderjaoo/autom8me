package com.docs.git;

import java.util.Arrays;

import com.docs.git.model.Version;
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
        int major = 0;
        int minor = 0;
        int patch = 0;

        Version version = new Version(major, minor, patch);

        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();
        GeminiService geminiService = new GeminiService();

        // captura os commits do git
        List<GitCommitDTO> commits = gitLogService.getGitLogs();

        commits = commitService.classifyCommits(commits);

        for (GitCommitDTO commit : commits) {
            String msg = commit.getMessage().toLowerCase();

            if (msg.startsWith("refactor")) {
                version.addMajor();
            } else if (msg.startsWith("feat")) {
                version.addMinor();
            } else if (msg.startsWith("fix")) {
                version.addPatch();
            }
        }

        String tag = "v"+version.toString();//"v0.2.1";
        int[] versionArray = version.toArray();
        System.out.println("Versao: " + Arrays.toString(versionArray) + " - Tag: " + tag);
        // Release Notes tradicional
        String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, versionArray, tag, language);
        
        //System.out.println(releaseNotesTemplate+"\n");

        String intelligentReleaseNotes = geminiService.generateResponse(releaseNotesTemplate);
        //System.out.println(intelligentReleaseNotes);
    }
}
