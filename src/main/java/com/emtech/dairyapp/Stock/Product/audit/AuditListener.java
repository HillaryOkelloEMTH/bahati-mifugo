package com.emtech.dairyapp.Stock.Product.audit;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.persistence.PostLoad;

@Component
public class AuditListener {

    private AuditService getAuditService() {
        return SpringContext.getBean(AuditService.class);
    }


    private Object previousState;

    @PostLoad
    public void capturePreviousState(Object object) {
        try {
            // Clone object before modifications
            previousState = object.getClass().getDeclaredConstructor().newInstance();
            org.springframework.beans.BeanUtils.copyProperties(object, previousState);
        } catch (Exception e) {
            previousState = null;
        }
    }

    @PrePersist
    public void prePersist(Object object) {
        getAuditService().logAction("POST", object.getClass().getSimpleName(), object, "Created a new record");
    }

    @PreUpdate
    public void preUpdate(Object object) {
        getAuditService().logUpdateAction(object.getClass().getSimpleName(), previousState, object);
    }

    @PreRemove
    public void preRemove(Object object) {
        getAuditService().logAction("DELETE", object.getClass().getSimpleName(), object, "Deleted record");
    }
}
