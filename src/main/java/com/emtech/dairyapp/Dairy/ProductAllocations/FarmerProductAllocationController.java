package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/farmer/allocations")
public class FarmerProductAllocationController {

    @Autowired
    private FarmerProductAllocationService service;


    @PostMapping("add")
    public ResponseEntity<EntityResponse> addAllocations(@RequestBody FarmerProductAllocations allocations){
        EntityResponse response = service.addFarmerProductAllocations(allocations);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getFarmerProductAllocations(){
        EntityResponse response = service.fetchFarmerProductAllocations();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateFarmerProductAllocations(@RequestBody FarmerProductAllocations allocations){
        EntityResponse response = service.updateFarmerProductAllocations(allocations);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> revokeFarmerProductAllocations(@PathVariable Long id){
        EntityResponse response = service.revoke(id);
        return ResponseEntity.ok().body(response);
    }
}
