package com.emtech.dairyapp.Dairy.Supply;


import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
    public ResponseEntity<EntityResponse> getMemberCollections(@RequestParam Integer farmerNo){
        EntityResponse response = collectionService.getCollectionsByMember(farmerNo);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String date ){
        EntityResponse response = collectionService.getCollectionsByDate(collectorId,date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/date/event")
    public ResponseEntity<EntityResponse> getBuyingBUying(@RequestParam Long collectorId, @RequestParam String date ){
        EntityResponse response = collectionService.getCollectionsByDateandEvent(collectorId,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String from , @RequestParam String to ){
        EntityResponse response = collectionService.getCollectionsByCollectorAndDate(collectorId,from,to);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("fetch-by/collectorId/date-range/payment-status")
    public ResponseEntity<?> fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(@RequestParam Long collectorId, @RequestParam String from , @RequestParam String to, @RequestParam Character paymentStatus ){
        List<CollectionsData> collectionsData = collectionService.fetchCollectorsCollectionsHistoryByDateRangeAndPaymentStatus(collectorId, from, to, paymentStatus);
        EntityResponse response = new EntityResponse();
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(collectionsData);

        return new ResponseEntity<>(response, HttpStatus.OK);
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
    @GetMapping("per/farmer")
    public ResponseEntity<EntityResponse> getFarmerCollections(@RequestParam Long farmerId){
        EntityResponse response = collectionService.getCollectionByFarmer(farmerId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("per/collector")
    public ResponseEntity<EntityResponse> getCollectorCollections(@RequestParam Long collectorId){
        EntityResponse response = collectionService.getCollectionByColector(collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("per/collector/buying")
    public ResponseEntity<EntityResponse> getBuyingCollectorCollections(@RequestParam Long collectorId){
        EntityResponse response = collectionService.getBuyingCollectionByColector(collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("specific/date")
    public ResponseEntity<EntityResponse> getDateCollections(@RequestParam String date){
        EntityResponse response = collectionService.getCollectionsBySpecificDate(date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("day/records")
    public ResponseEntity<EntityResponse> getDateRecords(@RequestParam String date){
        EntityResponse response = collectionService.getDayRecords(date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("date/range")
    public ResponseEntity<EntityResponse> getDateRangeCollections(@RequestParam String fromdate,@RequestParam String toDate){
        EntityResponse response = collectionService.getCollectionsByDateRange(fromdate, toDate);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("all")
    public ResponseEntity<EntityResponse> getAllCollections(){
        EntityResponse response = collectionService.getAllCollections();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/routes")
    public ResponseEntity<?> getCollectorRoutes(@RequestParam Long collectorId,@RequestParam String date){
        EntityResponse response = collectionService.getCollectionsRoutes(collectorId,date);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/today")
    public ResponseEntity<?> getCollectorColnsPerDay(@RequestParam String date){
        EntityResponse response = collectionService.getDayCollectionsPerColector(date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("amount")
    public ResponseEntity<?> getCollectionAmount(@RequestParam Long farmerId,@RequestParam Character paymentFlag){
        EntityResponse response = collectionService.getAmountPerPaymentStatus(paymentFlag,farmerId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/collections/price")
    public ResponseEntity<?> getCollectorColnsPerDay(@RequestParam Long farmerId,@RequestParam Character paymentFlag){
        EntityResponse response = collectionService.getCollectionRecordsByPrice(paymentFlag,farmerId);
        return ResponseEntity.ok().body(response);
    }



}
