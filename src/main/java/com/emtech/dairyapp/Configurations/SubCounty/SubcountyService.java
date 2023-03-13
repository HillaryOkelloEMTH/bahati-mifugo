package com.emtech.dairyapp.Configurations.SubCounty;


import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class SubcountyService {

//    @Value("${pagination.pageSize}")
//    private Integer pageSize;
    @Autowired
    private SubcountyRepo subcountyRepo;


    public EntityResponse addSubcounty(Subcounty subcounty) {
        log.info("saving Subcounty...");
        EntityResponse response = new EntityResponse<>();
        try {

            subcountyRepo.save(subcounty);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(subcounty);
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        } catch (Exception e) {
            log.error(e.getMessage());

            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(subcounty);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        return response;
    }

    public EntityResponse getSubcounty(Integer pageNo) {
        EntityResponse response = new EntityResponse<>();
        try {

//            Pageable paging = PageRequest.of(pageNo, pageSize);
//            Page<Subcounty> data = subcountyRepo.findAll(paging);
            List<com.emtech.dairyapp.Configurations.Interfaces.Subcounty> all = subcountyRepo.selectAll();

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


    public EntityResponse update(Subcounty subcounty) {
        EntityResponse response = new EntityResponse<>();
        try {
            Subcounty data = subcountyRepo.save(subcounty);
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

    public EntityResponse getSubcounty(Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            Optional<Subcounty> data = subcountyRepo.findById(id);
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

    public EntityResponse deleteSubCounty(Long id) {
        EntityResponse response = new EntityResponse();
        try {
                subcountyRepo.deleteById(id);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

    }



}
