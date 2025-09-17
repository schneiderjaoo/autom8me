package com.docs.git;

import com.docs.git.dto.GitCommitDTO;
import com.docs.git.service.CommitService;
import com.docs.git.service.GitLogService;
import com.docs.git.service.ReleaseNotesService;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class GitApplication {

    public static void main(String[] args) {
        GitLogService gitLogService = new GitLogService();
        CommitService commitService = new CommitService(new com.docs.git.service.ClassifierService());
        ReleaseNotesService releaseNotes = new ReleaseNotesService();

        List<GitCommitDTO> commits = gitLogService.getGitLogs();

        commits = commitService.classifyCommits(commits);

        System.out.println("Commits capturados:");
        for (GitCommitDTO commit : commits) {
            System.out.printf("%s | %s | %s | %s%n",
                    commit.getSha(),
                    commit.getType(),
                    commit.getMessage(),
                    commit.getDate());
        }

        String releaseNotesTemplate = releaseNotes.generateReleaseNotes(commits, "beta", "0.2.0");

        System.out.println("\nRelease Notes:\n" + releaseNotesTemplate);
    }
}
