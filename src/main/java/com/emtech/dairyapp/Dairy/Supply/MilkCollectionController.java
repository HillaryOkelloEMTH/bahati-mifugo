package com.emtech.dairyapp.Dairy.Supply;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("api/v1/collections")
public class MilkCollectionController {

    @Autowired
    private MilkCollectionService collectionService;

    @PostMapping("add")
    public ResponseEntity<EntityResponse> addNewRecord(@RequestBody MilkCollections collections){
        EntityResponse response = collectionService.newcollection(collections);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getColllections(){
        EntityResponse response = collectionService.getCollection();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("{id}")
    public ResponseEntity<EntityResponse> getColllectionsById(@PathVariable Long id){
        EntityResponse response = collectionService.getCollectionsById(id);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateCollections(@RequestBody MilkCollections collections){
        EntityResponse response = collectionService.updateCollections(collections);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deleteCollections(@PathVariable Long id){
        EntityResponse response = collectionService.deleteCollections(id);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer")
    public ResponseEntity<EntityResponse> getMemberCollections(@RequestParam Long id){
        EntityResponse response = collectionService.getCollectionsByMember(id);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String date ){
        EntityResponse response = collectionService.getCollectionsByDate(collectorId,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String from , @RequestParam String to ){
        EntityResponse response = collectionService.getCollectionsByCollectorAndDate(collectorId,from,to);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("collector")
    public ResponseEntity<EntityResponse> getCollectionsByCollectors(@RequestParam Long collectorId){
        EntityResponse response = collectionService.getCollectionsByColelctor(collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collections/tracking")
    public ResponseEntity<EntityResponse> getCollectionsTrackers(){
        EntityResponse response = collectionService.collectionsTracker();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("collections/today/collector")
    public ResponseEntity<EntityResponse> getDailyCollections(){
        EntityResponse response = collectionService.collectionsDailyRecordsPerColelctor();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collections/today")
    public ResponseEntity<EntityResponse> getTodayCollections(){
        EntityResponse response = collectionService.collectionsTodayRecords();
        return ResponseEntity.ok().body(response);
    }

}
