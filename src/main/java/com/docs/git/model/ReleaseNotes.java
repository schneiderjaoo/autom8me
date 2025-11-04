package com.docs.git.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "release_notes")
public class ReleaseNotes {
    
    @Id
    private String id;
    
    private String tag;
    private String version;
    private String language;
    private String releaseNotesTemplate;
    private String intelligentReleaseNotes;
    private LocalDateTime createdAt;
    
    public ReleaseNotes() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ReleaseNotes(String tag, String version, String language, 
                       String releaseNotesTemplate, String intelligentReleaseNotes) {
        this.tag = tag;
        this.version = version;
        this.language = language;
        this.releaseNotesTemplate = releaseNotesTemplate;
        this.intelligentReleaseNotes = intelligentReleaseNotes;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getReleaseNotesTemplate() {
        return releaseNotesTemplate;
    }
    
    public void setReleaseNotesTemplate(String releaseNotesTemplate) {
        this.releaseNotesTemplate = releaseNotesTemplate;
    }
    
    public String getIntelligentReleaseNotes() {
        return intelligentReleaseNotes;
    }
    
    public void setIntelligentReleaseNotes(String intelligentReleaseNotes) {
        this.intelligentReleaseNotes = intelligentReleaseNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

