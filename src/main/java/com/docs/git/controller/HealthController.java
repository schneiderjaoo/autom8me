package com.docs.git.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/check")
    public String healthCheck() {
        return "GitDocs ativo!";
    }
}