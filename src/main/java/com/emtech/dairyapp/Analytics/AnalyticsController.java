package com.emtech.dairyapp.Analytics;

import com.emtech.dairyapp.Dairy.Supply.MilkCollectionService;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/collections/analytics")
public class AnalyticsController {

    private final  CollectionsAnalytics analyticsService;
    @Autowired
    private MilkCollectionService collectionService;


    public AnalyticsController(CollectionsAnalytics analyticsService) {
        this.analyticsService = analyticsService;
    }


    @GetMapping("date")
    public ResponseEntity<EntityResponse> getCollectionsAnalysisPerDate(@RequestParam String date){
        EntityResponse response = analyticsService.getCollectionByDate(date);
        return  ResponseEntity.ok().body(response);
    }
    @GetMapping("year")
    public ResponseEntity<EntityResponse> getCollectionsAnalysisPerDate(@RequestParam Integer year){
        EntityResponse response = analyticsService.getCollectionByYear(year);
        return  ResponseEntity.ok().body(response);
    }
    @GetMapping("quantity/location")
    public ResponseEntity<?> getquanityperLocation(){
        EntityResponse response = analyticsService.getCollectionPerLocation();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/collections")
    public ResponseEntity<?> getCollectorData(@RequestParam Integer year,@RequestParam Integer month,@RequestParam Long collectorId){
        EntityResponse response = analyticsService.getCollectionByMontheAndYear(year,month,collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/sessions")
    public ResponseEntity<?> getCollectorSessionsData(@RequestParam Integer year,@RequestParam Integer month,@RequestParam Long collectorId){
        EntityResponse response = analyticsService.getCollectionByMonthAndYearandSesson(year,month,collectorId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("roleUsers")
    public ResponseEntity<?> roleUsers(@RequestParam Long roleId){
        EntityResponse response = collectionService.getRoleusers(roleId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collection/month")
    public ResponseEntity<?> getCollectionsPerMonth(@RequestParam Integer year,@RequestParam Long collectorId){
        EntityResponse response = analyticsService.getCollectionByMonth(year, collectorId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collection/collector/month")
    public ResponseEntity<?> getCollectorCollectionsPerMonth(@RequestParam Integer year,@RequestParam Integer month){
        EntityResponse response = analyticsService.getCollectorCollectionsPerMonth(year, month);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collection/count/collector")
    public ResponseEntity<?> getCollectorCountPerCollector(@RequestParam Integer year,@RequestParam Long collectorId){
        EntityResponse response = analyticsService.getCollectionCount(year,collectorId);
        return ResponseEntity.ok().body(response);
    }
}
