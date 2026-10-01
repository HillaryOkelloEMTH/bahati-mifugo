package com.emtech.dairyapp.GoogleLocations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("api/v1/location")
public class LocationController {

    private final LocationServices locationServices;


    public LocationController(LocationServices locationServices) {
        this.locationServices = locationServices;
    }


    @GetMapping("place/details")
    public ResponseEntity<?>  getLocationDetails(@RequestParam String latitude,@RequestParam String longitude) throws IOException {
        return ResponseEntity.ok().body(locationServices.getLocations(latitude,longitude));
    }
 }
