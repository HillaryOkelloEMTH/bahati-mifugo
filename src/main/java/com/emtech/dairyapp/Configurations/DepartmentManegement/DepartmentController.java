package com.emtech.dairyapp.Configurations.DepartmentManegement;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/department")
public class DepartmentController {


    private final DepartmentService departmentService;
    private final DepartmentRepo departmentRepo;

    public DepartmentController(DepartmentService departmentService, DepartmentRepo departmentRepo) {
        this.departmentService = departmentService;
        this.departmentRepo = departmentRepo;
    }


    @PostMapping("add")
    public ResponseEntity<EntityResponse> adddepartment(@RequestBody Department department){
        EntityResponse response = departmentService.addDepartment(department);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("get")
    public ResponseEntity<EntityResponse> getDepartments(){
        EntityResponse response = departmentService.fetchDepartment();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateDepartment(@RequestBody Department department){
        EntityResponse response = departmentService.updateDepartment(department);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> deleteDepartment(@PathVariable Long id){
        EntityResponse response = departmentService.deleteDepatrtment(id);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/permanent/{id}")
    public ResponseEntity<EntityResponse> deleteDepartmentParmanent(@PathVariable Long id){
        EntityResponse response = departmentService.deleteDepatrtmentParmanent(id);
        return ResponseEntity.ok().body(response);
    }


}


