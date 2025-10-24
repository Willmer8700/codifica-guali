package com.umg.codificaguali.repository;

import com.umg.codificaguali.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    
    List<Track> findByActiveTrue();
    
    Optional<Track> findByName(String name);
    
    @Query(value = "SELECT * FROM tracks WHERE active = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Track> findRandomActiveTrack();
}