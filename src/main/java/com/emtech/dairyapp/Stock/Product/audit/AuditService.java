package com.emtech.dairyapp.Stock.Product.audit;

import com.emtech.dairyapp.Auth.Utilities.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.emtech.dairyapp.Auth.Utilities.UserInfo.username;

@Service
public class AuditService {
    @Autowired
     private AuditRepository auditRepository;
    @Autowired
    private ObjectMapper objectMapper;


    public AuditService(AuditRepository auditRepository, ObjectMapper objectMapper, JWTUtil jwtUtil) {
        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String modelName, Object object, String details) {
        Audit audit = new Audit();
        audit.setAction(action);
        audit.setModelName(modelName);
        audit.setTimestamp(ZonedDateTime.now());
        String currentUsername = username();
        audit.setUsername(currentUsername.isEmpty() ? "UnknownUser" : currentUsername);

        try {
            audit.setDetails(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            audit.setDetails("Error serializing object: " + e.getMessage());
        }

        auditRepository.save(audit);
    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdateAction(String modelName, Object before, Object after) {
        Audit audit = new Audit();
        audit.setAction("UPDATE");
        audit.setModelName(modelName);
        audit.setTimestamp(ZonedDateTime.now());
        audit.setUsername("Staff");
        try {
            String beforeJson = (before != null) ? objectMapper.writeValueAsString(before) : "null";
            String afterJson = objectMapper.writeValueAsString(after);
            audit.setDetails("Before: " + beforeJson + " | After: " + afterJson);
        } catch (Exception e) {
            audit.setDetails("Error serializing objects: " + e.getMessage());
        }

        auditRepository.save(audit);
    }
}
