package com.docs.git.repository;

import com.docs.git.model.ReleaseNotes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReleaseNotesRepository extends MongoRepository<ReleaseNotes, String> {
    
    List<ReleaseNotes> findByTag(String tag);
    
    List<ReleaseNotes> findByVersion(String version);
    
    List<ReleaseNotes> findByLanguage(String language);
}

