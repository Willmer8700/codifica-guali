package com.umg.codificaguali.repository;

import com.umg.codificaguali.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    
    List<GameSession> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(g) FROM GameSession g WHERE g.createdAt BETWEEN :start AND :end")
    Long countSessionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(g) FROM GameSession g WHERE g.completed = true AND g.createdAt BETWEEN :start AND :end")
    Long countCompletedSessionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(g) FROM GameSession g WHERE g.completed = false AND g.createdAt BETWEEN :start AND :end")
    Long countFailedSessionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}