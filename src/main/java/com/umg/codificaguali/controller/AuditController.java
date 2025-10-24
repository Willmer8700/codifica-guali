package com.umg.codificaguali.controller;

import com.umg.codificaguali.model.AuditLog;
import com.umg.codificaguali.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    
    private final AuditService auditService;
    
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLog>> getLogsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(auditService.getLogsByUsername(username));
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<AuditLog>> getLogsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getLogsBetween(start, end));
    }
}