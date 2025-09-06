package com.docs.git.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReleaseNotesDTO {
    private String version;
    private String content;
    private Integer commitCount;
}