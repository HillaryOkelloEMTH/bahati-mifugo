package com.emtech.dairyapp.Dairy.FloatTracking;


import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("api/v1/float")
public class FloatManagerController {
    private final FloatManagerRepo floatManagerRepo;

    public FloatManagerController(FloatManagerRepo floatManagerRepo) {
        this.floatManagerRepo = floatManagerRepo;
    }

    @PostMapping("allocate")
    public ResponseEntity<?> allocateAmount(@RequestBody FloatAllocationRequest request, Authentication authentication) {
        EntityResponse response = new EntityResponse<>();
        Optional<FloatManager> manager = floatManagerRepo.findByCollectorId(request.getCollectorId());
        if (manager.isPresent()) {
            Double rmfloat = manager.get().getFloatAmount();
            Double rmbalance = manager.get().getBalance();
            manager.get().setDate(new Date());
            manager.get().setAllocatedBy(authentication.getName());
            manager.get().setCollectorId(request.getCollectorId());
            manager.get().setFloatAmount(request.getAllocationAmount() + rmfloat);
            manager.get().setBalance(request.getAllocationAmount() + rmbalance);
            floatManagerRepo.save(manager.get());
            response.setEntity(manager);
        } else {
            FloatManager newfm = new FloatManager();
            newfm.setDate(new Date());
            newfm.setAllocatedBy(authentication.getName());
            newfm.setCollectorId(request.getCollectorId());
            newfm.setFloatAmount(request.getAllocationAmount());
            floatManagerRepo.save(newfm);
            response.setEntity(newfm);
        }


        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get/allocations")
    public ResponseEntity<?> getAllocations() {
        FloatManager manager = new FloatManager();
        manager.setDate(new Date());
        ;
        List<FloatManagerRepo.FloatData> list = floatManagerRepo.getAllFloatManager();

        EntityResponse response = new EntityResponse<>();
        response.setEntity(list);
        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get/allocations/collector")
    public ResponseEntity<?> getAllocationsPerCollector(@RequestParam Long collectorId) {
        EntityResponse response = new EntityResponse();
        Optional<FloatManager> floatM = floatManagerRepo.findByCollectorId(collectorId);
        if (floatM.isPresent()) {
            response.setEntity(floatM);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
        } else {
//            response.setEntity(floatM);
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("update/allocations")
    public ResponseEntity<?> updateAllocations(@RequestParam FloatManager f) {
        EntityResponse response = new EntityResponse();
        FloatManager floatM = floatManagerRepo.save(f);
        response.setEntity(floatM);
        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("delete/allocations")
    public ResponseEntity<EntityResponse> deleteAllocations(@RequestParam Long allocationId) {
        EntityResponse response = new EntityResponse();
        Optional<FloatManager> floatM = floatManagerRepo.findById(allocationId);
        if (floatM.isPresent()) {
           floatM.get().setDeletedFlag(CONSTANTS.YES);
           floatManagerRepo.save(floatM.get());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());

        } else {

            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }

        return ResponseEntity.ok().body(response);
    }


}
