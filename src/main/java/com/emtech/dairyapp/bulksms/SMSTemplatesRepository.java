package com.emtech.dairyapp.bulksms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SMSTemplatesRepository extends JpaRepository<SMSTemplates,Long> {
    Optional<SMSTemplates> findByTemplateName(String templateName);
    List<SMSTemplates> findTop5ByOrderByIdDesc();
}
