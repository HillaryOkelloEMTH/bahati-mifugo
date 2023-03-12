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
    public ResponseEntity<EntityResponse> addSubcounty(@RequestBody PickUpLocations location){
        log.info("receiving request ...");
        EntityResponse response = service.addPickUpLocations(location);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("fetch")
    public ResponseEntity<?> getCostituencies(@RequestParam Integer pageNo){
        EntityResponse response = service.getPickUpLocations(pageNo);
        return  ResponseEntity.ok().body(response);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCostituencies(@RequestBody PickUpLocations locations){
        EntityResponse response = service.update(locations);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> GetCostituencyById(@PathVariable Long id){
        EntityResponse response = service.getPickUpLocations(id);
        return  ResponseEntity.ok().body(response);
    }


}
