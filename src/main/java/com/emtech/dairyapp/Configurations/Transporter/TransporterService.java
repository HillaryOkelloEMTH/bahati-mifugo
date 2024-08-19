package com.emtech.dairyapp.Configurations.Transporter;

import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.stereotype.Service;

@Service
public interface TransporterService {
    EntityResponse<String> addTransporter(Long routeId, String username);
    EntityResponse<?> getTransporterRoutes(Long transporterId);
    EntityResponse<?> removeTransporter(String username, Long routeId);
    EntityResponse<?> getTransporters();
}
