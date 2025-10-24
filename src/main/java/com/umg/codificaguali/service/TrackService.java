package com.umg.codificaguali.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.codificaguali.model.Track;
import com.umg.codificaguali.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackService {
    
    private final TrackRepository trackRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    
    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }
    
    public List<Track> getActiveTracks() {
        return trackRepository.findByActiveTrue();
    }
    
    public Optional<Track> getTrackById(Long id) {
        return trackRepository.findById(id);
    }
    
    public Optional<Track> getRandomTrack() {
        return trackRepository.findRandomActiveTrack();
    }
    
    @Transactional
    public Track createTrack(Track track, String username) {
        Track saved = trackRepository.save(track);
        auditService.log(username, "CREATE_TRACK", "Created track: " + track.getName());
        return saved;
    }
    
    @Transactional
    public Track updateTrack(Long id, Track trackDetails, String username) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        
        track.setName(trackDetails.getName());
        track.setGridData(trackDetails.getGridData());
        track.setGridWidth(trackDetails.getGridWidth());
        track.setGridHeight(trackDetails.getGridHeight());
        track.setStartX(trackDetails.getStartX());
        track.setStartY(trackDetails.getStartY());
        track.setStartDirection(trackDetails.getStartDirection());
        track.setActive(trackDetails.getActive());
        
        Track updated = trackRepository.save(track);
        auditService.log(username, "UPDATE_TRACK", "Updated track: " + track.getName());
        return updated;
    }
    
    @Transactional
    public void deleteTrack(Long id, String username) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        
        auditService.log(username, "DELETE_TRACK", "Deleted track: " + track.getName());
        trackRepository.deleteById(id);
    }
    
    public String exportTrack(Long id) throws JsonProcessingException {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        return objectMapper.writeValueAsString(track);
    }
    
    @Transactional
    public Track importTrack(String jsonData, String username) throws JsonProcessingException {
        Map<String, Object> trackData = objectMapper.readValue(jsonData, Map.class);
        
        Track track = new Track();
        track.setName((String) trackData.get("name"));
        track.setGridData((String) trackData.get("gridData"));
        track.setGridWidth((Integer) trackData.get("gridWidth"));
        track.setGridHeight((Integer) trackData.get("gridHeight"));
        track.setStartX((Integer) trackData.get("startX"));
        track.setStartY((Integer) trackData.get("startY"));
        track.setStartDirection((String) trackData.get("startDirection"));
        track.setActive(true);
        
        Track saved = trackRepository.save(track);
        auditService.log(username, "IMPORT_TRACK", "Imported track: " + track.getName());
        return saved;
    }
}