package com.emtech.dairyapp.Configurations.PickUpLocations;


import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PickUpLocationService {

    @Value("${pagination.pageSize}")
    private Integer pageSize;

    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;


    public EntityResponse addPickUpLocations(PickUpLocations pickUpLocations) {
        log.info("saving PickUpLocations...");
        EntityResponse response = new EntityResponse<>();
        try {
            pickUpLocationsRepo.save(pickUpLocations);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(pickUpLocations);
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(pickUpLocations);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

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


    public EntityResponse update(PickUpLocations PickUpLocations) {
        EntityResponse response = new EntityResponse<>();
        try {
            PickUpLocations data = pickUpLocationsRepo.save(PickUpLocations);
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
}
