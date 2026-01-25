package com.edu.erpbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        // Returns HTTP 200 OK with a simple JSON body
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}