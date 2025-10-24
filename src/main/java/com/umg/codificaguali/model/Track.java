package com.umg.codificaguali.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String gridData; // JSON string representing the track grid
    
    @Column(nullable = false)
    private Integer gridWidth = 5;
    
    @Column(nullable = false)
    private Integer gridHeight = 4;
    
    @Column(nullable = false)
    private Integer startX;
    
    @Column(nullable = false)
    private Integer startY;
    
    @Column(nullable = false)
    private String startDirection; // "UP", "DOWN", "LEFT", "RIGHT"
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}