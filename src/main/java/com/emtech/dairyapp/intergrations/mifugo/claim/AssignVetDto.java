package com.emtech.dairyapp.intergrations.mifugo.claim;

import lombok.Data;

@Data
public class AssignVetDto {
    private Long vetId;
    private String vetName;
    private String vetUsername;
    private String vetPhone;
    private String vetKvbNumber;
    private String assignedBy;
    private String notes;
}

