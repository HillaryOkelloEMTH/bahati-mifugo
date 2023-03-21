package com.emtech.dairyapp.Configurations.FarmerManagement;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/farmer")
public class FarmerController {




    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }




    @PostMapping("add")
    public ResponseEntity<EntityResponse> addfarmer(@RequestBody Farmer farmer){
        EntityResponse response = farmerService.addFarmer(farmer);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getfarmers(){
        EntityResponse response = farmerService.fetchFarmer();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updatefarmer(@RequestBody Farmer farmer){
        EntityResponse response = farmerService.updateFarmer(farmer);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deletefarmer(@PathVariable Long id){
        EntityResponse response = farmerService.deleteFarmer(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("farmers/ward")
    public ResponseEntity<EntityResponse> getfarmersByward(@RequestParam Long wardId){
        EntityResponse response = farmerService.fetchFarmersByward(wardId);
        return ResponseEntity.ok().body(response);
    }



}
