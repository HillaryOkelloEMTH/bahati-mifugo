package com.emtech.dairyapp.Configurations.servicesConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicesConfigService {

    @Autowired
    private ServicesConfigRepository servicesConfigRepository;

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);

    public ServicesConfig addService(ServicesConfig servicesConfig) {
        try {
            LOG.error("CREATING SERVICE ::: {}", servicesConfig.toString());
            return servicesConfigRepository.save(servicesConfig);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE CREATING SERVICE::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public List<ServicesConfig> fetchAllServices() {
        try {
            LOG.error("FETCHING ALL SERVICES ::: ");
            return servicesConfigRepository.findAll();
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALL SERVICES ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public Optional<ServicesConfig> fetchServiceById(Long id) {
        try {
            LOG.error("FETCHING SERVICE WITH ID ::: {}", id);
            return servicesConfigRepository.findById(id);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING SERVICE ::: {}", exc.getLocalizedMessage());
            return Optional.empty();
        }
    }

    public List<ServicesConfig> fetchServiceByStatus(String status) {
        try {
            LOG.error("FETCHING ALL SERVICES BY STATUS ::: {}", status);
            return servicesConfigRepository.findAllByStatus(status);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALL SERVICES BY STATUS ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }


    public List<ServicesConfig> fetchServicingStatus(String status) {
        try {
            LOG.error("FETCHING ALL SERVICES BY SERVICING STATUS ::: {}", status);
            return servicesConfigRepository.findAllByServicingStatus(status);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE FETCHING ALL SERVICES BY SERVICING STATUS ::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public ServicesConfig updateService(ServicesConfig servicesConfig) {
        try {
            LOG.error("UPDATING SERVICE ::: {}", servicesConfig.toString());
            return servicesConfigRepository.save(servicesConfig);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE UPDATING SERVICE::: {}", exc.getLocalizedMessage());
            return null;
        }
    }

    public void deleteService(Long id) {
        try {
            LOG.error("DELETING SERVICE WITH ID ::: {}", id);
            servicesConfigRepository.deleteById(id);
        }catch (Exception exc){
            LOG.error("ERROR ENCOUNTERED WHILE DELETING SERVICE ::: {}", exc.getLocalizedMessage());
        }
    }
}
