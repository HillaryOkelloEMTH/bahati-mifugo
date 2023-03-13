package com.emtech.dairyapp.Configurations.VehicleManagement;

import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
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
public class VehicleService {

    @Autowired
    private VehicleRepo repo;


    public EntityResponse addVehicle(Vehicle vehicle) {
        EntityResponse response = new EntityResponse();
        try {

            vehicle.setCreatedAt(new Date());
            repo.save(vehicle);

            response.setEntity(vehicle);
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            response.setStatusCode(HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setEntity(vehicle);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }

    public EntityResponse getvehicle() {
        EntityResponse response = new EntityResponse();
        try {


            List<Vehicle> vehicle = repo.findAll();

            response.setEntity(vehicle);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }


    public EntityResponse updateVehicle(Vehicle vehicle) {
        EntityResponse response = new EntityResponse();
        try {

            vehicle.setCreatedAt(new Date());
            repo.save(vehicle);

            response.setEntity(vehicle);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setEntity(vehicle);
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        }
        return response;
    }

    public EntityResponse getvehicle(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Vehicle> vehicle = repo.findById(id);
            if (vehicle.isPresent()) {
                response.setEntity(vehicle);
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
            } else {
                response.setEntity(vehicle);
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

    public EntityResponse deletevehicle(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Vehicle> vehicle = repo.findById(id);
            if (vehicle.isPresent()) {


                if (vehicle.get().getAssignmentStatus().equalsIgnoreCase("YES")) {

                    response.setMessage("vehicle is still assigned to " + vehicle.get().getAssignedTo() + " sub_counties");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(vehicle);
                } else {
                    repo.deleteById(id);
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                }
            }else {

                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());

            }

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

    }

    public EntityResponse assignVehicle(Long id,String username) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Vehicle> vehicle = repo.findById(id);
            if (vehicle.isPresent()) {
                log.info("Vehicle found ...");
                log.info("Assigning vehicle to ${username}");
                vehicle.get().setAssignedTo(username);
                vehicle.get().setAssignmentDate(new Date());
                vehicle.get().setAssignmentStatus("YES");
                repo.save(vehicle.get());


                response.setEntity(vehicle);
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
            } else {
                response.setEntity(vehicle);
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





}
