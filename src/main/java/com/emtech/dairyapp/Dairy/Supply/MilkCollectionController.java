package com.emtech.dairyapp.Dairy.Supply;


import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Supply.bulkuploads.BulkSupplyService;
import com.emtech.dairyapp.Dairy.Supply.returns.MilkReturnService;
import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("api/v1/collections")
public class MilkCollectionController {

    @Autowired
    private MilkCollectionService collectionService;

    @Autowired
    private BulkSupplyService bulkSupplyService;

    @Autowired
    private MilkReturnService milkReturnService;

    @PostMapping("add")
    public ResponseEntity<EntityResponse> addNewRecord(@RequestBody MilkCollections collections){
        EntityResponse response = collectionService.newcollection(collections);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/add/bulk")
    public Mono<ResponseEntity<?>> uploadBulkDeliveries(ServerWebExchange exchange, @RequestParam String username, @RequestParam String mobile) {
        return exchange.getMultipartData()
                .flatMap(multipart -> {
                    FilePart filePart = (FilePart) multipart.getFirst("file");
                    if (filePart == null) {
                        Map<String, Object> errBody = new HashMap<>();
                        errBody.put("message", "File is empty");
                        errBody.put("status", "400");
                        return Mono.just(ResponseEntity.badRequest().body(errBody));
                    }
                    return bulkSupplyService.uploadBulkDeliveries(filePart, username, mobile)
                            .map(response -> ResponseEntity.status(response.getStatusCode()).body(response));
                });
    }

    @GetMapping("route-summary/{routeId}/{month}/{year}")
    public ResponseEntity<?> getRouteSummary(@PathVariable Long routeId, @PathVariable int month, @PathVariable String year) {
        var response = collectionService.getRouteDeliverySummary(routeId, month, year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("bulk/by-date/{from}/{to}")
    public ResponseEntity<?> getBulkUploads(@PathVariable String from, @PathVariable String to) {
        var response = bulkSupplyService.getUploadsByDateRange(from, to);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("farmer/deliveries/{farmerNo}/{from}/{to}")
    public ResponseEntity<?> getFarmerDeliveries(@PathVariable Integer farmerNo, @PathVariable String from, @PathVariable String to) {
        var response = collectionService.getFarmerDeliveries(farmerNo, from, to);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping("return/{id}")
    public ResponseEntity<?> returnDelivery(@PathVariable Long id) {
        var response = milkReturnService.returnDelivery(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
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
    public ResponseEntity<EntityResponse> updateCollections(@RequestBody UpdateMilkCollectiorequest collections){
        EntityResponse response = collectionService.updateCollections(collections);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deleteCollections(@PathVariable Long id){
        EntityResponse response = collectionService.deleteCollections(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("fetch/collections/farmers/current/previous")
    public ResponseEntity<?> fetchCurrentAndPreviousCollectionsAnFarmersCount(@RequestParam  Integer collectorId, @RequestParam String todayDate, @RequestParam String previousDayDate){
        EntityResponse response = collectionService.fetchCurrentAndPreviousCollectionsAndFarmersCount(collectorId, todayDate, previousDayDate);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("collector/date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String date){
        EntityResponse response = collectionService.getCollectionsByDate(collectorId,date);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("collector/farmer-no/date")
    public ResponseEntity<EntityResponse> filterTodaysCollections(@RequestParam Long collectorId, @RequestParam String date, @RequestParam(required = false) String farmerNo, @RequestParam(required = false) String session){
        if (farmerNo == null || farmerNo.isEmpty()){
            EntityResponse response = collectionService.filterTodaysCollectionsBySession(collectorId, date, session);
            return ResponseEntity.ok().body(response);
        }else {
            if (session == null || session.isEmpty()){
                session = "%%";
            }

            EntityResponse response = collectionService.filterTodaysCollections(collectorId,date, farmerNo, session);
            return ResponseEntity.ok().body(response);
        }
    }


    @GetMapping("collector-purchases/date-range/")
    public ResponseEntity<EntityResponse> getCollectorsPurchasesByDateRange(@RequestParam Long collectorId, @RequestParam String from,@RequestParam String to ){
        EntityResponse response = collectionService.getCollectorsPurchasesByDateRange(collectorId,from,to);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector-purchases/date/")
    public ResponseEntity<EntityResponse> getCollectorsPurchasesByDate(@RequestParam Long collectorId, @RequestParam String date ){
        EntityResponse response = collectionService.getCollectorsPurchasesByDate(collectorId,date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("date")
    public ResponseEntity<EntityResponse> getCollections(@RequestParam Long collectorId, @RequestParam String from , @RequestParam String to ){
        EntityResponse response = collectionService.getCollectionsByCollectorAndDate(collectorId,from,to);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("filtered-collections/date")
    public ResponseEntity<EntityResponse> getFilteredCollections(@RequestParam Long collectorId, @RequestParam(required = false) String farmerNo, @RequestParam(required = false) String session, @RequestParam String from , @RequestParam String to ){
        if (farmerNo.isEmpty()){
            farmerNo = "%%";
        }
        if (session.isEmpty() || session.equalsIgnoreCase("All Sessions")){
            session = "%%";
        }

        EntityResponse response = collectionService.getFilteredCollections(collectorId, farmerNo, session, from,to);
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
    public ResponseEntity<?> getDateRecords(@RequestParam String date){
        var response = collectionService.getDayRecords(date);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("date/range")
    public ResponseEntity<EntityResponse> getDateRangeCollections(@RequestParam String fromdate,@RequestParam String toDate){
        EntityResponse response = collectionService.getCollectionsByDateRange(fromdate, toDate);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("date/range/records")
    public ResponseEntity<EntityResponse> getDateRangeRecords(@RequestParam String fromdate,@RequestParam String toDate){
        EntityResponse response = collectionService.getDateRangeRecords(fromdate, toDate);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("all")
    public ResponseEntity<EntityResponse> getAllCollections(){
        EntityResponse response = collectionService.getAllCollections();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("collection-details/id")
    public ResponseEntity<EntityResponse> getCollectionDetailsByCollectionId(@RequestParam Long id){
        EntityResponse response = collectionService.getCollectionDetailsByCollectionId(id);
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
    public ResponseEntity<?> getCollectionAmount(@RequestParam Integer farmerNo,@RequestParam Character paymentFlag){
        EntityResponse response = collectionService.getAmountPerPaymentStatus(paymentFlag,farmerNo);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("farmer/collections/price")
    public ResponseEntity<?> getCollectorColnsPerDay(@RequestParam Integer farmerNo,@RequestParam Character paymentFlag){
        EntityResponse response = collectionService.getCollectionRecordsByPrice(paymentFlag,farmerNo);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("pickupLocations")
    public ResponseEntity<?> getCollectorbyPickUpLocations(@RequestParam Long pickUpLocation, @RequestParam String from, @RequestParam String to){
        EntityResponse response = collectionService.getCollectionByPickUpLocation(pickUpLocation, from, to);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("record/pickupLocations")
    public ResponseEntity<?> getRecordsbyPickUpLocations(@RequestParam Long pickUpLocation, @RequestParam String from, @RequestParam String to){
        EntityResponse response = collectionService.getPickUpLocationRecords(pickUpLocation, from, to);
        return ResponseEntity.ok().body(response);
    }

//filter route by date and range
    @GetMapping("/route/filter")
    public ResponseEntity<?> getCollectionsByRouteAndDate(
            @RequestParam Long routeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        EntityResponse response = collectionService.getCollectionsByRouteAndDate(routeId, startDate, endDate);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
//filter farmer by date range
@GetMapping("farmer")
public ResponseEntity<EntityResponse> getMemberCollections(
        @RequestParam Integer farmerNo,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
) {
    EntityResponse response = collectionService.getCollectionsByMember(farmerNo, startDate, endDate);
    return ResponseEntity.ok().body(response);
}


//farmer status active or inactive per route
@GetMapping("/route/farmer-status")
public ResponseEntity<Map<String, Object>> getFarmersStatusByRoute(
        @RequestParam Long routeId,
        @RequestParam int month,
        @RequestParam int year) {
    Map<String, Object> result = collectionService.getFarmerStatusByRoute(routeId, month, year);
    return ResponseEntity.ok(result);
}

//farmer status
@GetMapping("/farmer-status/monthly")
public ResponseEntity<Map<String, Object>> getMonthlyFarmerStatus(
        @RequestParam int month,
        @RequestParam int year) {
    Map<String, Object> response = collectionService.getFarmerStatusByMonth(month, year);
    return ResponseEntity.ok(response);
}



    @GetMapping("records/route")
    public ResponseEntity<?> getRouteRecords(@RequestParam Long routeId){
        EntityResponse response = collectionService.getRouteRecords(routeId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("records/all")
    public ResponseEntity<?> getAllRecords(){
        var response = collectionService.getAllCollectionsRecords();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("summary/routes")
    public ResponseEntity<?> getRoutedSummary(){
        EntityResponse response = collectionService.getRouteSummary();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("pickuplocations/date")
    public ResponseEntity<?> getCollectorbyPickUpLocationsAndDate(@RequestParam  Long pickUpLocation,@RequestParam String date){
        EntityResponse response = collectionService.getCollectionByPickUpCollationsAndDate(pickUpLocation,date);
        return ResponseEntity.ok().body(response);
    }




}
