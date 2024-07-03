package com.emtech.dairyapp.Configurations.FarmerManagement;


import com.emtech.dairyapp.Analytics.LinkedStringInteger;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("api/v1/farmer")
public class FarmerController {




    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }




    @PostMapping("add")
    public ResponseEntity<EntityResponse> addfarmer(@RequestBody Farmer farmer){
        log.info("recieving request ...adding farmer...");
        EntityResponse response = farmerService.addFarmer(farmer);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getfarmers(){
        EntityResponse response = farmerService.fetchFarmer();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("all")
    public ResponseEntity<EntityResponse> getAllfarmers(){
        EntityResponse response = farmerService.fetchFarmers();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updatefarmer(@RequestBody Farmer farmer){
        EntityResponse response = farmerService.updateFarmer(farmer);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("update/route/{farmerNo}/{routeId}")
    public ResponseEntity<?> updateFarmerRoute(@PathVariable Integer farmerNo, @PathVariable Long routeId) {
        var response = farmerService.updateFarmerRoute(farmerNo, routeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
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
    @GetMapping("farmers/details")
    public ResponseEntity<EntityResponse> getfarmerDetails(@RequestParam Long farmerId){
        EntityResponse response = farmerService.fetchFarmerById(farmerId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/id")
    public ResponseEntity<EntityResponse> findById(@RequestParam Long farmerId){
        EntityResponse response = farmerService.findById(farmerId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmers/collector")
    public ResponseEntity<EntityResponse> getfarmerByCollector(@RequestParam Long collectorId){
        EntityResponse response = farmerService.fetchFarmerByCollector(collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmers/location")
    public ResponseEntity<?> getfarmerByCollector(){
        LinkedStringInteger response = farmerService.farmersPerLocation();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("membernumber")
    public ResponseEntity<?> getfarmerBymemberNumber(@RequestParam Integer farmer_number){
        EntityResponse response = farmerService.fetchFarmerByMemberNO(farmer_number);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmerNumber")
    public ResponseEntity<?> getfarmerByfarmerNumber(@RequestParam Integer farmer_number,@RequestParam Long collectorId){

        EntityResponse response = farmerService.fetchFarmerByfarmerNo(farmer_number,collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("accruals")
    public ResponseEntity<?> getfarmerAccruedAmount(@RequestParam Long farmerId){
        EntityResponse response = farmerService.fetchFarmerAccrualAmount(farmerId);
        return ResponseEntity.ok().body(response);
    }

}
