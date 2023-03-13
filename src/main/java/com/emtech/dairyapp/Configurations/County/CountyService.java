package com.emtech.dairyapp.Configurations.County;

import com.emtech.dairyapp.Configurations.SubCounty.Subcounty;
import com.emtech.dairyapp.Configurations.SubCounty.SubcountyRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class CountyService {

    @Autowired
    private CountyRepo countyRepo;
    @Autowired
    private SubcountyRepo subcountyRepo;


    public EntityResponse addCounty(County county) {
        EntityResponse response = new EntityResponse();
        try {

            county.setCreatedAt(new Date());
            countyRepo.save(county);

            response.setEntity(county);
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            response.setStatusCode(HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setEntity(county);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }

    public EntityResponse getSubcounties() {
        EntityResponse response = new EntityResponse();
        try {


            List<County> subcounties = countyRepo.findAll();

            response.setEntity(subcounties);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }


    public EntityResponse updateCounty(County county) {
        EntityResponse response = new EntityResponse();
        try {

            county.setCreatedAt(new Date());
            countyRepo.save(county);

            response.setEntity(county);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setEntity(county);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }

    public EntityResponse getCounty(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<County> county = countyRepo.findById(id);
            if (county.isPresent()) {
                response.setEntity(county);
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
            } else {
                response.setEntity(county);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }

    public EntityResponse deleteCounty(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            List<Subcounty> subcounties = subcountyRepo.findByCountyFk(id);
            if (subcounties.size() > 1) {
                response.setMessage("County cannot be deleted. County is attached to " + subcounties.size() + " sub_counties");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(subcounties);
            } else {
                countyRepo.deleteById(id);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
            }

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

    }


}
