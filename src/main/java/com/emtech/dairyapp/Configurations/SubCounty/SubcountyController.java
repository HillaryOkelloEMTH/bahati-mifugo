package com.emtech.dairyapp.Configurations.SubCounty;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/Subcounty")
public class SubcountyController {

    @Autowired
    private SubcountyService subcountyService;



    @PostMapping("add")
    public ResponseEntity<EntityResponse> addSubcounty(@RequestBody Subcounty subcounty){
        log.info("receiving request ...");
      EntityResponse response = subcountyService.addSubcounty(subcounty);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("fetch")
    public ResponseEntity<?> getCostituencies(@RequestParam Integer pageNo){
        EntityResponse response = subcountyService.getSubcounty(pageNo);
        return  ResponseEntity.ok().body(response);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateCostituencies(@RequestBody Subcounty subcounty){
        EntityResponse response = subcountyService.update(subcounty);
        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> GetCostituencyById(@PathVariable Long id){
        EntityResponse response = subcountyService.getSubcounty(id);
        return  ResponseEntity.ok().body(response);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<EntityResponse> deleteSubcountyById(@PathVariable Long id) {

        EntityResponse response = subcountyService.deleteSubCounty(id);
        return ResponseEntity.ok().body(response);

    }

}
