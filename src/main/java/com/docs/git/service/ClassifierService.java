package com.docs.git.service;

import com.docs.git.dto.GitCommitDTO;
import com.docs.git.enums.CommitType;
import org.springframework.stereotype.Component;

@Component
public class ClassifierService {

    public CommitType classify(String msg) {
        String lowerMsg = msg.toLowerCase();

        if (lowerMsg.startsWith("feat") || lowerMsg.contains("feature")) {
            return CommitType.FEAT;
        } else if (lowerMsg.startsWith("fix") || lowerMsg.contains("bug")) {
            return CommitType.FIX;
        } else if (lowerMsg.startsWith("hotfix")) {
            return CommitType.HOTFIX;
        } else if (lowerMsg.startsWith("docs") || lowerMsg.contains("doc")) {
            return CommitType.DOCS;
        } else if (lowerMsg.startsWith("refactor")) {
            return CommitType.REFACTOR;
        } else if (lowerMsg.startsWith("test") || lowerMsg.contains("testing")) {
            return CommitType.TEST;
        } else if (lowerMsg.startsWith("release") || lowerMsg.contains("version")) {
            return CommitType.RELEASE;
        } else {
            return CommitType.DOCS; // Default to DOCS for unrecognized messages
        }
    }
}