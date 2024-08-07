package com.emtech.dairyapp.Configurations.Transporter;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TransporterServiceImpl implements TransporterService {

    final private  TransporterRepository transporterRepo;

    TransporterServiceImpl(TransporterRepository transporterRepository) {
        this.transporterRepo = transporterRepository;
    }

    @Override
    public EntityResponse<String> addTransporter(Long routeId, String username) {
        EntityResponse<String> response = new EntityResponse<>();

        try {
            boolean exists = transporterRepo.existsByUsername(username);

            if (exists) {
                response.setMessage("Transporter already exists");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

            Transporter transporter = new Transporter();
            transporter.setUsername(username);
            transporter.setRouteId(routeId);

            transporterRepo.save(transporter);

            response.setMessage("Transporter added successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity("successful");
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @Override
    public EntityResponse<?> getTransporterRoutes(Long transporterId) {
        EntityResponse<List<TransporterRepository.TransporterRoutes>> response = new EntityResponse<>();

        try {
            List<TransporterRepository.TransporterRoutes> routes = transporterRepo.getTransporterRoutes(transporterId);

            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Found "+routes.size()+" records");
            response.setEntity(routes);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    @Override
    public EntityResponse<?> removeTransporter(String username, Long routeId) {
        return null;
    }
}
