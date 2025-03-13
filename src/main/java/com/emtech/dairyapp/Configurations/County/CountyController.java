package com.emtech.dairyapp.Configurations.County;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("api/v1/county")
public class CountyController {



    @Autowired
    private CountyService countyService;


    @PostMapping("add")
    public ResponseEntity<EntityResponse> addcounty(@RequestBody County county) {
        log.info("receiving request...");
        System.out.println(county);
        EntityResponse response = countyService.addCounty(county);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("get")
    public ResponseEntity<EntityResponse> getcounty() {
        log.info("receiving request ...");

        EntityResponse response = countyService.getSubcounties();
        return ResponseEntity.ok().body(response);

    }

    @PutMapping("update")
    public ResponseEntity<EntityResponse> updatecounty(@RequestBody County county, @RequestParam Long countyId) {

        EntityResponse response = countyService.updateCounty(county, countyId);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{id}")
    public ResponseEntity<EntityResponse> getcountyById(@PathVariable Long id) {

        EntityResponse response = countyService.getCounty(id);
        return ResponseEntity.ok().body(response);

    }
    @DeleteMapping("{id}")
    public ResponseEntity<EntityResponse> deletecountyById(@PathVariable Long id) {

        EntityResponse response = countyService.deleteCounty(id);
        return ResponseEntity.ok().body(response);

    }
}
