package com.emtech.dairyapp.Configurations.DepartmentManegement;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@Slf4j
public class DepartmentService {

    private final DepartmentRepo departmentRepo;

    public DepartmentService(DepartmentRepo departmentRepo) {
        this.departmentRepo = departmentRepo;
    }


    public EntityResponse addDepartment(Department department){
        log.info("Adding new department ...");
     EntityResponse response = new EntityResponse();
     try{
         department.setCreatedOn(new Date());
         department.setDeletedFlag(CONSTANTS.NO);
         departmentRepo.save(department);
         log.info("Saving department ...");
         response.setEntity(department);
         response.setStatusCode(HttpStatus.CREATED.value());
         response.setMessage(HttpStatus.CREATED.getReasonPhrase());
         return response;


     }catch (Exception e){
         log.error("Error: " + e.getLocalizedMessage());
         response.setStatusCode(HttpStatus.BAD_REQUEST.value());
         response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
         return response;
     }
 }
    public EntityResponse fetchDepartment() {
        log.info("Fetching departments ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Department> departments = departmentRepo.findByDeletedFlag(CONSTANTS.NO);
            if(departments.size()>0) {
                log.info("Departments Found "+ "("+departments.size()+")");
                response.setEntity(departments);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            }else {
                log.info("Departments Not Found "+ "("+departments.size()+")");
                response.setEntity(departments);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse updateDepartment(Department department) {
        EntityResponse response = new EntityResponse();
        try {
            department.setCreatedOn(new Date());
           Department dep= departmentRepo.save(department);
            response.setEntity(dep);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            return response;


        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse deleteDepatrtment(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Department> department = departmentRepo.findById(id);
            if(department.isPresent()){
                department.get().setDeletedFlag(CONSTANTS.YES);
                department.get().setDeletedOn(new Date());
                departmentRepo.save(department.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Department deleted Successfully");
                return response;

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Department with id "+id+"Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse deleteDepatrtmentParmanent(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<Department> department = departmentRepo.findById(id);
            if(department.isPresent()){
                departmentRepo.deleteById(id);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Department deleted Permanently");
                return response;

            }else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Department with id "+id+"Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }




}
