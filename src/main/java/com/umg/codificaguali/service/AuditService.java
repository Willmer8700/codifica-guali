package com.umg.codificaguali.service;

import com.umg.codificaguali.model.AuditLog;
import com.umg.codificaguali.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Transactional
    public void log(String username, String action, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
    
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
    
    public List<AuditLog> getLogsByUsername(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }
    
    public List<AuditLog> getLogsBetween(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
}