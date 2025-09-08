package com.docs.git.service;

import org.springframework.stereotype.Service;
import com.docs.git.dto.GitCommitDTO;
import com.docs.git.service.ClassifierService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommitService {

    private final ClassifierService classifierService;

    public CommitService(ClassifierService classifierService) {
        this.classifierService = classifierService;
    }

    public List<GitCommitDTO> classifyCommits(List<GitCommitDTO> commits) {
        for (GitCommitDTO commit : commits) {
            commit.setType(classifierService.classify(commit.getMessage()));
        }
        return commits;
    }
}