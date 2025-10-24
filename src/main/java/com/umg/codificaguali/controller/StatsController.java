package com.umg.codificaguali.controller;

import com.umg.codificaguali.service.GameSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatsController {
    
    private final GameSessionService gameSessionService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(gameSessionService.getStatistics(start, end));
    }
    
    @PostMapping("/game/session")
    public ResponseEntity<Void> recordSession(@RequestBody Map<String, Object> sessionData, HttpServletRequest request) {
        Long trackId = Long.valueOf(sessionData.get("trackId").toString());
        Boolean completed = (Boolean) sessionData.get("completed");
        Integer attempts = (Integer) sessionData.get("attempts");
        String sessionIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        gameSessionService.recordSession(trackId, completed, attempts, sessionIp, userAgent);
        return ResponseEntity.ok().build();
    }
}