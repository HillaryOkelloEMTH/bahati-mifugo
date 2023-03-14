package com.emtech.dairyapp.Configurations.DepartmentManegement;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepo extends JpaRepository<Department,Long> {


    List<Department> findByDeletedFlag(Character flag);


}
