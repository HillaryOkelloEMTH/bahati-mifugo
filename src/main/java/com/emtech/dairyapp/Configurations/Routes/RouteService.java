package com.emtech.dairyapp.Configurations.Routes;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RouteService {
    
    
    private final RouteRepo repo;


    public RouteService(RouteRepo repo) {
        this.repo = repo;
    }

    public EntityResponse addRoute(Route route){
        log.info("Adding new Route ...");
        EntityResponse response = new EntityResponse();
        try{
            route.setCreatedOn(new Date());
            route.setDeletedFlag(CONSTANTS.NO);
            repo.save(route);
            log.info("Saving Route ...");
            response.setEntity(route);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            return response;


        }catch (Exception e){
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse fetchRoute() {
        log.info("Fetching Routes ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Route> Routes = repo.findByDeletedFlag(CONSTANTS.NO);
            if(Routes.size()>0) {
                log.info("Routes Found "+ "("+Routes.size()+")");
                response.setEntity(Routes);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Routes Not Found "+ "("+Routes.size()+")");
                response.setEntity(Routes);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse<?> fetchCollectorRoutes(Long collectorId) {
        log.info("Fetching Routes ...");
        EntityResponse<Object> response = new EntityResponse<>();
        try {
            List<RouteRepo.CollectorRoutes> Routes = repo.getRouteByCollector(collectorId);
            if(!Routes.isEmpty()) {
                log.info("Collector Routes Found "+ "("+Routes.size()+")");
                response.setEntity(Routes);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Routes Not Found "+ "("+ 0 +")");
                response.setEntity(Routes);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse updateRoute(Route route) {
        EntityResponse response = new EntityResponse();
        try {
            route.setCreatedOn(new Date());
            Route r= repo.save(route);
            response.setEntity(r);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            return response;


        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse deleteDepatrtment(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Route> Route = repo.findById(id);
            if(Route.isPresent()){
                Route.get().setDeletedFlag(CONSTANTS.YES);
                Route.get().setDeletedOn(new Date());
                repo.save(Route.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Route deleted Successfully");
                return response;

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Route with id "+id+"Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
}
