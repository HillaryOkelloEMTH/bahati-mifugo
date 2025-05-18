package com.emtech.dairyapp.Configurations.Routes;

import com.emtech.dairyapp.Configurations.Collectors.Collector;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String route;
    private Date createdOn;
    private Character deletedFlag= CONSTANTS.NO;
    private Date deletedOn;
    private Character activeFlag = CONSTANTS.YES;
}
