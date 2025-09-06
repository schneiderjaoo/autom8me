package com.docs.git.dto;

import com.docs.git.enums.CommitType;

public class GitCommitDTO {
    private String sha;
    private String message;
    private String date;
    private CommitType type;

    public GitCommitDTO(String sha, String message, CommitType type, String date) {
        this.sha = sha;
        this.message = message;
        this.type = type;
        this.date = date;
    }

    // Getters and Setters
    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CommitType getType() {
        return type;
    }

    public void setType(CommitType type) {
        this.type = type;
    }
}