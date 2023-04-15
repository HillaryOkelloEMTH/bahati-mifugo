package com.emtech.dairyapp.Configurations.servicesConfig;


import com.emtech.dairyapp.Response.EntityResponse;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/v1/services")
@Api(tags = "Services Configuration Controller")
public class ServicesConfigController {

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);
    private final EntityResponse response = new EntityResponse();

    @Autowired
    private ServicesConfigService configService;

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody ServicesConfig servicesConfig){

        ServicesConfig service = configService.addService(servicesConfig);
        if (service != null){
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else {
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("fetch-all")
    public ResponseEntity<?> fetchAllServices(){
        List<ServicesConfig> services = configService.fetchAllServices();
        if (services.size() > 0){
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else if (services.size() <= 0){
            response.setMessage("No Record Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("fetch/by-id")
    public ResponseEntity<?> fetchServiceById(@RequestParam Long id){
        Optional<ServicesConfig> service = configService.fetchServiceById(id);
        if (service.isPresent()){
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.setMessage("No Record Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("fetch/by-status")
    public ResponseEntity<?> fetchServiceByStatus(@RequestParam String status){
        List<ServicesConfig> services = configService.fetchServiceByStatus(status);
        if (services.size() > 0){
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else if (services.size() <= 0){
            response.setMessage("No Record Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("fetch/by-servicing-status")
    public ResponseEntity<?> fetchServicingStatus(@RequestParam String status){
        List<ServicesConfig> services = configService.fetchServicingStatus(status);
        if (services.size() > 0){
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else if (services.size() <= 0){
            response.setMessage("No Record Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity(services);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else {
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @PutMapping("update")
    public ResponseEntity<?> updateService(@RequestBody ServicesConfig  servicesConfig){
        ServicesConfig service = configService.updateService(servicesConfig);
        if (service != null){
            response.setMessage("Record Updated Successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(service);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> deleteService(@RequestParam Long id){
        configService.deleteService(id);
        response.setMessage("Record deleted Successfully");
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity("[]");

        return new ResponseEntity<>(response, HttpStatus.OK);

    }




}
