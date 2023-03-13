package com.emtech.dairyapp.Configurations.VehicleManagement;

import com.emtech.dairyapp.Configurations.County.County;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("apo/v1/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("add")
    public ResponseEntity<EntityResponse> addVehicle(@RequestBody Vehicle vehicle) {
        log.info("receiving request...");
        EntityResponse response = vehicleService.addVehicle(vehicle);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("get")
    public ResponseEntity<EntityResponse> getvehicle() {
        log.info("receiving request ...");

        EntityResponse response = vehicleService.getvehicle();
        return ResponseEntity.ok().body(response);

    }

    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateVehicle(@RequestBody Vehicle vehicle) {

        EntityResponse response = vehicleService.updateVehicle(vehicle);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{id}")
    public ResponseEntity<EntityResponse> getVahilceBYid(@PathVariable Long id) {

        EntityResponse response = vehicleService.getvehicle(id);
        return ResponseEntity.ok().body(response);

    }
    @DeleteMapping("{id}")
    public ResponseEntity<EntityResponse> deleteVehicle(@PathVariable Long id) {

        EntityResponse response = vehicleService.deletevehicle(id);
        return ResponseEntity.ok().body(response);

    }


}
