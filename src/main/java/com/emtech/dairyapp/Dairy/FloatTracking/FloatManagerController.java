package com.emtech.dairyapp.Dairy.FloatTracking;


import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        EntityResponse response = new EntityResponse<>();
        Optional<FloatManager> manager = floatManagerRepo.findByCollectorId(request.getCollectorId());
        if(manager.isPresent()) {
            Double rmfloat = manager.get().getFloatAmount();
            manager.get().setDate(new Date());
            manager.get().setCollectorId(request.getCollectorId());
            manager.get().setFloatAmount(request.getAllocationAmount()+rmfloat);
            floatManagerRepo.save(manager.get());
            response.setEntity(manager);
        }else {
            FloatManager newfm = new FloatManager();
            newfm.setDate(new Date());
            newfm.setCollectorId(request.getCollectorId());
            newfm.setFloatAmount(request.getAllocationAmount());
            floatManagerRepo.save(newfm);
            response.setEntity(newfm);
        }



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
