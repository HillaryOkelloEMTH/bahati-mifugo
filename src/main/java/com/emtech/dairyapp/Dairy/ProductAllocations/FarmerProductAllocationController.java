package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.ProductAllocations.dto.ProductRequestDto;
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
    public ResponseEntity<?> addAllocations(@RequestBody ProductRequestDto productRequest){
        EntityResponse<?> response = service.addFarmerProductAllocations(productRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getFarmerProductAllocations(){
        EntityResponse response = service.fetchFarmerProductAllocations();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get/{locationId}/{month}/{year}")
    public ResponseEntity<EntityResponse<?>> getMccFarmerProductAllocations(@PathVariable Long locationId, @PathVariable Integer month, @PathVariable String year){
        EntityResponse<?> response = service.fetchMccFarmerProductAllocations(locationId, month, year);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer")
    public ResponseEntity<EntityResponse<?>> getFarmerAllocations(@RequestParam Integer farmerNo){
        EntityResponse<?> response = service.fetchFarmerAllocations(farmerNo);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("date")
    public ResponseEntity<EntityResponse> getAllocationsByDate(@RequestParam String date){
        EntityResponse response = service.fetchAllocationsByDate(date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/date")
    public ResponseEntity<EntityResponse> getFarmerAllocationsyDate(@RequestParam Integer farmerNo,@RequestParam String date){
        EntityResponse response = service.fetchFarmerAllocationsBYDate(farmerNo,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("farmer/paymentstatus")
    public ResponseEntity<EntityResponse> getFarmerAllocationsPerPaymentStatus(@RequestParam Integer farmerNo,@RequestParam Character paymentStatus){
        EntityResponse response = service.fetchFarmerAllocationsByPaymentStatus(farmerNo,paymentStatus);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("type")
    public ResponseEntity<EntityResponse> getFarmerAllocationsPertype(@RequestParam String type){
        EntityResponse response = service.fetchFarmerProductAllocationsPerType(type);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("verify")
    public ResponseEntity<?> approveAllocation(@RequestParam Long id,@RequestParam String status){
        var  response = service.updateStatus(id,status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
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
    @GetMapping("farmer/accruals")
    public ResponseEntity<EntityResponse> getFarmerAccruals(@RequestParam Integer farmerNo,@RequestParam Character paymentStatus){
        EntityResponse response = service.getFarmerAccruals(farmerNo,paymentStatus);
        return ResponseEntity.ok().body(response);
    }
}
