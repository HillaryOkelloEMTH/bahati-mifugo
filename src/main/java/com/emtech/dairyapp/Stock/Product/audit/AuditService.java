package com.emtech.dairyapp.Stock.Product.audit;

import com.emtech.dairyapp.Auth.Utilities.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Properties;


import static com.emtech.dairyapp.Auth.Utilities.UserInfo.username;

@Service
@Slf4j
public class AuditService {
    @Autowired
     private AuditRepository auditRepository;
    @Autowired
    private ObjectMapper objectMapper;



    public AuditService(AuditRepository auditRepository, ObjectMapper objectMapper) {
        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }
    private String extractId(Object object) {
        if (object == null) return "null";
        try {
            Method getIdMethod = object.getClass().getMethod("getId");
            Object idValue = getIdMethod.invoke(object);
            return (idValue != null) ? idValue.toString() : "null";
        } catch (Exception e) {
            log.error("Could not extract ID from object: {}", e.getMessage());
            return "null";
        }
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String modelName, Object object, String details) {
        String objectId = extractId(object);


        Audit audit = new Audit();
        audit.setAction(action);
        audit.setModelName(modelName);
        audit.setTimestamp(ZonedDateTime.now());


        audit.setMachineInfo(getMachineInfo());
        audit.setObjectId(objectId);

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            audit.setDetails(objectMapper.writeValueAsString(object));


        } catch (Exception e) {
            audit.setDetails("Error serializing object: " + e.getMessage());
        }

        auditRepository.save(audit);
    }



    public void logUpdateAction(String modelName, Object before, Object after) {
        String objectId = extractId(after);

        Audit audit = new Audit();
        audit.setAction("UPDATE");
        audit.setModelName(modelName);
        audit.setTimestamp(ZonedDateTime.now());
        audit.setMachineInfo(getMachineInfo());
        audit.setObjectId(objectId);


        try {
            String beforeJson = (before != null) ? objectMapper.writeValueAsString(before) : "null";
            String afterJson = objectMapper.writeValueAsString(after);
            audit.setDetails("Before: " + beforeJson + " | After: " + afterJson);
        } catch (Exception e) {
            audit.setDetails("Error serializing objects: " + e.getMessage());
        }

        auditRepository.save(audit);
    }


    public static String getMachineInfo() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostname = inetAddress.getHostName();
            String ipAddress = inetAddress.getHostAddress();

            Properties properties = System.getProperties();
            String osInfo = properties.getProperty("os.name") + " " + properties.getProperty("os.version");

            return "Hostname: " + hostname + ", IP: " + ipAddress + ", OS: " + osInfo;
        } catch (UnknownHostException e) {
            return "Could not retrieve machine info: " + e.getMessage();
        }
    }
}
