package com.umg.codificaguali.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umg.codificaguali.model.Track;
import com.umg.codificaguali.model.User;
import com.umg.codificaguali.repository.TrackRepository;
import com.umg.codificaguali.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if no users exist
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@codificaguali.com");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }
        
        // Create sample tracks if none exist
        if (trackRepository.count() == 0) {
            createSampleTracks();
            System.out.println("Sample tracks created");
        }
    }
    
    private void createSampleTracks() throws Exception {
        // Track 1: Simple straight path
        Track track1 = new Track();
        track1.setName("Camino Recto");
        track1.setGridWidth(5);
        track1.setGridHeight(4);
        track1.setStartX(0);
        track1.setStartY(3);
        track1.setStartDirection("RIGHT");
        track1.setActive(true);
        
        List<List<Boolean>> grid1 = new ArrayList<>();
        for (int y = 0; y < 4; y++) {
            List<Boolean> row = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                row.add(y == 3 && x < 5);
            }
            grid1.add(row);
        }
        track1.setGridData(objectMapper.writeValueAsString(grid1));
        trackRepository.save(track1);
        
        // Track 2: L-shaped path
        Track track2 = new Track();
        track2.setName("Camino en L");
        track2.setGridWidth(5);
        track2.setGridHeight(4);
        track2.setStartX(0);
        track2.setStartY(3);
        track2.setStartDirection("RIGHT");
        track2.setActive(true);
        
        List<List<Boolean>> grid2 = new ArrayList<>();
        for (int y = 0; y < 4; y++) {
            List<Boolean> row = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                row.add((y == 3 && x <= 2) || (x == 2 && y >= 1));
            }
            grid2.add(row);
        }
        track2.setGridData(objectMapper.writeValueAsString(grid2));
        trackRepository.save(track2);
        
        // Track 3: Square path
        Track track3 = new Track();
        track3.setName("Camino Cuadrado");
        track3.setGridWidth(5);
        track3.setGridHeight(4);
        track3.setStartX(1);
        track3.setStartY(3);
        track3.setStartDirection("RIGHT");
        track3.setActive(true);
        
        List<List<Boolean>> grid3 = new ArrayList<>();
        for (int y = 0; y < 4; y++) {
            List<Boolean> row = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                row.add((y == 3 && x >= 1 && x <= 3) || 
                       (y == 1 && x >= 1 && x <= 3) ||
                       (x == 1 && y >= 1 && y <= 3) ||
                       (x == 3 && y >= 1 && y <= 3));
            }
            grid3.add(row);
        }
        track3.setGridData(objectMapper.writeValueAsString(grid3));
        trackRepository.save(track3);
    }
}