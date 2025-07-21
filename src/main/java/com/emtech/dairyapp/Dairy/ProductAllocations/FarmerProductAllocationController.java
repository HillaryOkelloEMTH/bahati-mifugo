package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.ProductAllocations.dto.ProductRequestDto;
import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import jakarta.swing.text.html.parser.Entity;

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
    public ResponseEntity<?> getFarmerProductAllocations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        var response = service.fetchFarmerProductAllocations(page, size);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("date/range")
    private ResponseEntity<?> getAllocationsByDateRange(@RequestParam String from, @RequestParam String to) {
        var res = service.getAllocationsByDateRange(from, to);
        return new ResponseEntity<>(res, HttpStatusCode.valueOf(res.getStatusCode()));
    }

    @GetMapping("get/{locationId}/{month}/{year}")
    public ResponseEntity<EntityResponse<?>> getMccFarmerProductAllocations(@PathVariable Long locationId, @PathVariable Integer month, @PathVariable String year){
        var response = service.fetchMccFarmerProductAllocations(locationId, month, year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("get/route/{routeId}/{month}/{year}")
    public ResponseEntity<?> fetchRouteFarmerProductAllocations(@PathVariable Long routeId, @PathVariable Integer month, @PathVariable String year) {
        var response = service.fetchRouteFarmerProductAllocations(routeId, month, year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
//    @GetMapping("farmer")
//    public ResponseEntity<EntityResponse<?>> getFarmerAllocations(@RequestParam Integer farmerNo){
//        EntityResponse<?> response = service.fetchFarmerAllocations(farmerNo);
//        return ResponseEntity.ok().body(response);
//    }
    @GetMapping("farmer")
    public ResponseEntity<AllocationsResponse> getFarmerAllocations(@RequestParam Integer farmerNo){
        return service.fetchFarmerAllocations(farmerNo);
    }

    @GetMapping("date")
    public ResponseEntity<?> getAllocationsByDate(@RequestParam String date){
        var response = service.fetchAllocationsByDate(date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/date")
    public ResponseEntity<?> getFarmerAllocationsyDate(@RequestParam Integer farmerNo,@RequestParam String date){
        var response = service.fetchFarmerAllocationsBYDate(farmerNo,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("farmer/paymentstatus")
    public ResponseEntity<?> getFarmerAllocationsPerPaymentStatus(@RequestParam Integer farmerNo,@RequestParam Character paymentStatus){
        var response = service.fetchFarmerAllocationsByPaymentStatus(farmerNo,paymentStatus);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("type")
    public ResponseEntity<?> getFarmerAllocationsPertype(@RequestParam String type){
        var response = service.fetchFarmerProductAllocationsPerType(type);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("verify")
    private ResponseEntity<?> approveAllocation(@RequestParam Long id,@RequestParam String status){
        var  response = service.updateStatus(id,status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PutMapping("update")
    public ResponseEntity<?> updateFarmerProductAllocations(@RequestBody FarmerProductAllocations allocations){
        var response = service.updateFarmerProductAllocations(allocations);
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
