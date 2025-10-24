package com.umg.codificaguali.service;

import com.umg.codificaguali.model.GameSession;
import com.umg.codificaguali.model.Track;
import com.umg.codificaguali.repository.GameSessionRepository;
import com.umg.codificaguali.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    
    private final GameSessionRepository gameSessionRepository;
    private final TrackRepository trackRepository;
    
    @Transactional
    public void recordSession(Long trackId, Boolean completed, Integer attempts, String sessionIp, String userAgent) {
        Track track = trackRepository.findById(trackId).orElse(null);
        
        GameSession session = new GameSession();
        session.setTrack(track);
        session.setCompleted(completed);
        session.setAttempts(attempts);
        session.setSessionIp(sessionIp);
        session.setUserAgent(userAgent);
        
        gameSessionRepository.save(session);
    }
    
    public Map<String, Object> getStatistics(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalSessions = gameSessionRepository.countSessionsBetween(start, end);
        Long completedSessions = gameSessionRepository.countCompletedSessionsBetween(start, end);
        Long failedSessions = gameSessionRepository.countFailedSessionsBetween(start, end);
        
        stats.put("totalSessions", totalSessions);
        stats.put("completedSessions", completedSessions);
        stats.put("failedSessions", failedSessions);
        stats.put("successRate", totalSessions > 0 ? (completedSessions * 100.0 / totalSessions) : 0);
        
        return stats;
    }
    
    public List<GameSession> getSessionsBetween(LocalDateTime start, LocalDateTime end) {
        return gameSessionRepository.findByCreatedAtBetween(start, end);
    }
}