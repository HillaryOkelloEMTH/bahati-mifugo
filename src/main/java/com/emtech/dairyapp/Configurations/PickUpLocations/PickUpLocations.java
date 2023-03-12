package com.emtech.dairyapp.Configurations.PickUpLocations;

import com.emtech.dairyapp.Configurations.Collectors.Collector;
import com.emtech.dairyapp.Configurations.SubCounty.Ward;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PickUpLocations {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long ward_fk;

    @OneToMany(targetEntity = Ward.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id",referencedColumnName = "id")
    private List<Collector> collectors;


}
