package com.emtech.dairyapp.Dairy.FloatTracking;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/v1/float")
public class FloatManagerController {
    private  final FloatManagerRepo floatManagerRepo;

    public FloatManagerController(FloatManagerRepo floatManagerRepo) {
        this.floatManagerRepo = floatManagerRepo;
    }

    @PostMapping("allocate")
    public ResponseEntity<?> allocateAmount(@RequestBody FloatAllocationRequest request){
        FloatManager manager = new FloatManager();
        manager.setDate(new Date());
        manager.setCollectorId(request.getCollectorId());
        manager.setFloatAmount(request.getAllocationAmount());
        floatManagerRepo.save(manager);

        EntityResponse response = new EntityResponse<>();
        response.setEntity(manager);
        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        return  ResponseEntity.ok().body(response);
    }
    @GetMapping("get/allocations")
    public ResponseEntity<?> getAllocations(){
        FloatManager manager = new FloatManager();
        manager.setDate(new Date());;
        List<FloatManager> list =floatManagerRepo.findAll();

        EntityResponse response = new EntityResponse<>();
        response.setEntity(list);
        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        return  ResponseEntity.ok().body(response);
    }





}
