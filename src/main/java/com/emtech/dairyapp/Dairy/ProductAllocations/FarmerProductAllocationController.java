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
    @GetMapping("farmer")
    public ResponseEntity<EntityResponse> getFarmerAllocations(@RequestParam Long farmerId){
        EntityResponse response = service.fetchFarmerAllocations(farmerId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("date")
    public ResponseEntity<EntityResponse> getAllocationsByDate(@RequestParam String date){
        EntityResponse response = service.fetchAllocationsByDate(date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/date")
    public ResponseEntity<EntityResponse> getFarmerAllocationsyDate(@RequestParam Long farmerId,@RequestParam String date){
        EntityResponse response = service.fetchFarmerAllocationsBYDate(farmerId,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("farmer/paymentstatus")
    public ResponseEntity<EntityResponse> getFarmerAllocationsPerPaymentStatus(@RequestParam Long farmerId,@RequestParam Character paymentStatus){
        EntityResponse response = service.fetchFarmerAllocationsByPaymentStatus(farmerId,paymentStatus);
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
