package com.emtech.dairyapp.Configurations.PickUpLocations;


import com.emtech.dairyapp.Configurations.SubCounty.Subcounty;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("api/v1/pickuplocations")
public class PickUpLocationController {

   @Autowired
    private PickUpLocationService service;

    @PostMapping("add")
    public ResponseEntity<EntityResponse> addpickup(@RequestBody PickUpLocations location){
        log.info("receiving request ...");
        EntityResponse response = service.addPickUpLocations(location);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("fetch")
    public ResponseEntity<?> getpickup(){
        EntityResponse response = service.getPickUpLocations();
        return  ResponseEntity.ok().body(response);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePpickup(@RequestBody PickUpLocations locations){
        EntityResponse response = service.update(locations);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> GetpickupById(@PathVariable Long id){
        EntityResponse response = service.getPickUpLocationById(id);
        return  ResponseEntity.ok().body(response);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePickupById(@PathVariable Long id){
        EntityResponse response = service.deletePickUpLocationById(id);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("{username}/{wardId}")
    public ResponseEntity<?> GetpickupById(@PathVariable String username,@PathVariable Long wardId){
        EntityResponse response = service.getPickUpLocationByUsernameandWard(username,wardId);
        return  ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/ward")
    public ResponseEntity<?> GetpickupBywardandcollector(@RequestParam Long collectorId,@RequestParam Long wardId){
        EntityResponse response = service.getPickUpLocationsByColectorIdandWard(collectorId,wardId);
        return  ResponseEntity.ok().body(response);
    }

}
