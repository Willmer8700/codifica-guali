package com.umg.codificaguali.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.umg.codificaguali.model.Track;
import com.umg.codificaguali.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {
    
    private final TrackService trackService;
    
    @GetMapping("/random")
    public ResponseEntity<Track> getRandomTrack() {
        return trackService.getRandomTrack()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        return ResponseEntity.ok(trackService.getAllTracks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        return trackService.getTrackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody Track track, Authentication auth) {
        Track created = trackService.createTrack(track, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track track, Authentication auth) {
        Track updated = trackService.updateTrack(id, track, auth.getName());
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id, Authentication auth) {
        trackService.deleteTrack(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/export")
    public ResponseEntity<Map<String, String>> exportTrack(@PathVariable Long id) {
        try {
            String json = trackService.exportTrack(id);
            return ResponseEntity.ok(Map.of("data", json));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/import")
    public ResponseEntity<Track> importTrack(@RequestBody Map<String, String> payload, Authentication auth) {
        try {
            String jsonData = payload.get("data");
            Track imported = trackService.importTrack(jsonData, auth.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(imported);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}