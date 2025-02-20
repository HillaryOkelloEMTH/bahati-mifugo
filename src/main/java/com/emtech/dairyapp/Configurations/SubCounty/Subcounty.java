package com.emtech.dairyapp.Configurations.SubCounty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Subcounty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Long countyFk;
    @OneToMany(targetEntity = Ward.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "subcounty_id",referencedColumnName = "id")
    private List<Ward> wards;
}


