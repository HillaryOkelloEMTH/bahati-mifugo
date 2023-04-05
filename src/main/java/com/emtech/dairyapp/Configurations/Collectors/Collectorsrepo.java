package com.emtech.dairyapp.Configurations.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface Collectorsrepo extends JpaRepository<Collector,Long> {





}
