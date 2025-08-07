package com.emtech.dairyapp.Configurations.PickUpLocations;


import com.emtech.dairyapp.Configurations.Collectors.Collector;
import com.emtech.dairyapp.Configurations.FarmerManagement.BankDetails;
import com.emtech.dairyapp.Configurations.Interfaces.Locations;
import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import com.emtech.dairyapp.Configurations.Interfaces.PickUpPoints;
import com.emtech.dairyapp.Configurations.Routes.Route;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PickUpLocationService {

    @Value("${pagination.pageSize}")
    private Integer pageSize;

    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;

//
//    @Transactional
//    public EntityResponse addPickUpLocations(PickUpLocations pickUpLocations) {
//        log.info("saving PickUpLocations...");
//        EntityResponse response = new EntityResponse<>();
//        try {
//
//            List<Collector> collectors= pickUpLocations.getCollectors();
//            List<String> usernames=collectors.stream()
//                    .map(collector -> collector.getUsername())
//                    .collect(Collectors.toList());
//
//            long distinctCount = usernames.stream()
//                    .distinct()
//                    .count();
//            boolean hasDuplicates = distinctCount != collectors.size();
//            if(hasDuplicates){
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity(usernames);
//                response.setMessage("The Collectors Contains Duplicates");
//                return  response;
//            }
//            System.out.println("Debugger ------1");
//            PickUpLocations p = pickUpLocationsRepo.save(pickUpLocations);
//
//            for (Collector c:collectors ) {
//                System.out.println("debugger ------2");
//                pickUpLocationsRepo.updateCollectorInformation(p.getName(),c.getUsername());
//            }
//
//            System.out.println("debugger -------3");
//
//            response.setStatusCode(HttpStatus.CREATED.value());
//            response.setEntity(pickUpLocations);
//            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
//        } catch (Exception e) {
//            log.error(e.getMessage());
//
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            response.setEntity(pickUpLocations);
//            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
//        }
//
//        return response;
//    }
@Transactional
public EntityResponse addPickUpLocations(PickUpLocations pickUpLocations) {
    log.info("saving PickUpLocations...");
    EntityResponse response = new EntityResponse<>();

    List<Collector> collectors= pickUpLocations.getCollectors();
    List<String> usernames=collectors.stream()
            .map(Collector::getUsername)
            .collect(Collectors.toList());

    long distinctCount = usernames.stream().distinct().count();
    boolean hasDuplicates = distinctCount != collectors.size();
    if(hasDuplicates){
        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        response.setEntity(usernames);
        response.setMessage("The Collectors Contains Duplicates");
        return  response;
    }

    System.out.println("Debugger ------1");
    PickUpLocations p = pickUpLocationsRepo.save(pickUpLocations);

    for (Collector c : collectors) {
        System.out.println("debugger ------2");
        pickUpLocationsRepo.updateCollectorInformation(p.getName(), c.getUsername());
    }

    System.out.println("debugger -------3");

    response.setStatusCode(HttpStatus.CREATED.value());
    response.setEntity(pickUpLocations);
    response.setMessage(HttpStatus.CREATED.getReasonPhrase());
    return response;
}

    public EntityResponse getPickUpLocations() {
        EntityResponse response = new EntityResponse<>();
        try {

//            Pageable paging = PageRequest.of(pageNo, pageSize);
//            List<PickUpLocations> data = pickUpLocationsRepo.findAll(paging);
            List<PickUpLocation> all = pickUpLocationsRepo.getAllPickUpLocations();

            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setEntity(all);
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());

        }
        return response;
    }
    public EntityResponse getPickUpLocationsByColectorIdandWard(Long collectorid,Long wardId) {
        EntityResponse response = new EntityResponse<>();
        try {

            List<Locations> all = pickUpLocationsRepo.getPickUpLcoationsBywardandCollectorId(collectorid,wardId);

            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setEntity(all);
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());

        }
        return response;
    }


    public EntityResponse update(PickUpLocations pickUpLocations) {
        EntityResponse response = new EntityResponse<>();
        try {

            List<Collector> collectors= pickUpLocations.getCollectors();
            List<String> usernames=collectors.stream()
                    .map(collector -> collector.getUsername())
                    .collect(Collectors.toList());

            long distinctCount = usernames.stream()
                    .distinct()
                    .count();
            boolean hasDuplicates = distinctCount != collectors.size();
            if(hasDuplicates){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(usernames);
                response.setMessage("The Collectors Contains Duplicates");
                return  response;
            }
            PickUpLocations data = pickUpLocationsRepo.save(pickUpLocations);

            for (Collector c:collectors ) {
                pickUpLocationsRepo.updateCollectorInformation(data.getName(),c.getUsername());
            }

            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(data);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }


        return response;
    }

    public EntityResponse getPickUpLocationById(Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            Optional<PickUpLocations> data = pickUpLocationsRepo.findById(id);
            if(data.isPresent()){
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(data);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getRoutesById(Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            List<PickUpLocationsRepo.RouteInterface> data = pickUpLocationsRepo.getRoutesPerLocation(id);
            if(data.size()>0){
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(data);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse deletePickUpLocationById(Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            Optional<PickUpLocations> data = pickUpLocationsRepo.findById(id);
            if(data.isPresent()){
                pickUpLocationsRepo.deleteById(id);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getPickUpLocationByUsernameandWard(String username,Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            List<PickUpPoints> data = pickUpLocationsRepo.getPickUpLocations(username,id);
            if(data.size()>0){
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(data);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse<?> getPickUpLocationsByCollector(Long collectorId) {
        EntityResponse<List<Locations>> response = new EntityResponse<>();
        try {
            List<Locations> data = pickUpLocationsRepo.getPickUpLcoationsByCollectorId(collectorId);
            if(!data.isEmpty()){
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(data);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse<?> getTransporterPickUpLocations(Long transporterId) {
        EntityResponse<List<Locations>> response = new EntityResponse<>();

        try {
            List<Locations> data = pickUpLocationsRepo.getTransporterLocations(transporterId);

            response.setMessage("Retrieved "+data.size()+" records");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(data);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
