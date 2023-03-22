package com.emtech.dairyapp.Analytics;

import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/collections/analytics")
public class AnalyticsController {

    private final  CollectionsAnalytics analyticsService;


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

}
