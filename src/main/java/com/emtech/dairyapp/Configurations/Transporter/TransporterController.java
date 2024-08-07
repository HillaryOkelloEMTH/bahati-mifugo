package com.emtech.dairyapp.Configurations.Transporter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transporter")
public class TransporterController {
    @Autowired
    final private TransporterService transporterService;

    @PostMapping("add/{routeId}/{username}")
    public ResponseEntity<?> addTransporter(@PathVariable Long routeId, @PathVariable String username) {
        var response = transporterService.addTransporter(routeId, username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("get/{transporterId}")
    public ResponseEntity<?> getTransporterRoutes(@PathVariable Long transporterId) {
        var response = transporterService.getTransporterRoutes(transporterId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
