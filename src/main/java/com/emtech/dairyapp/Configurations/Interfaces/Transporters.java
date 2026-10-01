package com.emtech.dairyapp.Configurations.Interfaces;

import java.util.Date;

public interface Transporters {
    Long getId();
    String getUsername();
    Character getActive();
    Date getCreatedOn();
    String getRoute();
}
