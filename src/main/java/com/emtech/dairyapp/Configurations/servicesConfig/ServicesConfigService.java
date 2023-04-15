package com.emtech.dairyapp.Configurations.servicesConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicesConfigService {

    @Autowired
    private ServicesConfigRepository servicesConfigRepository;

    private final Logger LOG = LoggerFactory.getLogger(ServicesConfigController.class);

}
