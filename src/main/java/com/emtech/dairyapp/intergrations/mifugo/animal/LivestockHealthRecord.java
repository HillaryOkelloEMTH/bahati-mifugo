package com.emtech.dairyapp.intergrations.mifugo.animal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "livestock_health_records")
public class LivestockHealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String animalTag;

    private Long animalId;

    // Record Type: CLINICAL_TREATMENT, DEWORMING, ARTIFICIAL_INSEMINATION, CALVING, MASTITIS_TEST, SURGERY, ROUTINE_CHECKUP
    private String recordType;

    private String eventDate; // yyyy-MM-dd
    private String diagnosisOrProcedure;

    @Column(columnDefinition = "TEXT")
    private String treatmentDetails; // Drugs, dosages, semen straw details

    private String vetName;
    private String vetKvbNumber;

    // Outcome: RECOVERED, ONGOING_TREATMENT, SUCCESSFUL_CONCEPTION, COMPLICATED, FATAL
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}

