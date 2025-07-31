package com.emtech.dairyapp.Configurations.Routes;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/routes")
@Slf4j
public class RouteController {
    
    
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }


    @PostMapping("add")
    public ResponseEntity<EntityResponse> addroute(@RequestBody Route route){
        EntityResponse response = routeService.addRoute(route);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<?> getRoutes(@RequestParam Long subCountyFk){
        var response = routeService.fetchRoute(subCountyFk);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("collector/{collectorId}")
    public ResponseEntity<?> getColelctorRoutes(@PathVariable Long collectorId){
        var response = routeService.fetchCollectorRoutes(collectorId);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateroute(@RequestBody Route route){
        EntityResponse response = routeService.updateRoute(route);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deleteroute(@PathVariable Long id){
        EntityResponse response = routeService.deleteDepatrtment(id);
        return ResponseEntity.ok().body(response);
    }
}
