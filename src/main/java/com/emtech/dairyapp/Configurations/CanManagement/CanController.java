package com.emtech.dairyapp.Configurations.CanManagement;

import com.emtech.dairyapp.Response.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("api/v1/can")
public class CanController {


    @Autowired
    private CanRepo canRepo;


    @PostMapping("add")
    public ResponseEntity<EntityResponse> addNewCan(@RequestBody Can can) {
        StringBuilder sb = new StringBuilder();
        Integer count = canRepo.getAllCans();
        String canno="";
        if (count > 0) {
            Integer max = canRepo.getMaxValue();
            canno=sb.append("CAN-").append(max + 1).toString();

        } else {
            canno=sb.append("CAN-").append(0 + 1).toString();

        }
        can.setCanNo(canno);

        Can c = canRepo.save(can);

        EntityResponse response = new EntityResponse<>();
        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setEntity(c);

        return ResponseEntity.ok().body(response);


    }

    @GetMapping("fetch")
    public ResponseEntity<EntityResponse> getCans() {
        List<Can> cs = canRepo.findAll();
        EntityResponse response = new EntityResponse<>();
        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setEntity(cs);

        return ResponseEntity.ok().body(response);


    }


    @GetMapping("can")
    public ResponseEntity<EntityResponse> getCansByCanNumber(String canNumber) {
        Optional<Can> cs = canRepo.findByCanNo(canNumber);
        EntityResponse response = new EntityResponse<>();
        if(cs.isPresent()) {
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(cs.get());
        }else {
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return ResponseEntity.ok().body(response);


    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> update(@RequestBody Can can) {
        Can cs = canRepo.save(can);
        EntityResponse response = new EntityResponse<>();

            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(cs);

        return ResponseEntity.ok().body(response);


    }
    @DeleteMapping("delete")
    public ResponseEntity<EntityResponse> update(@RequestParam Long canId) {
        canRepo.deleteById(canId);
        EntityResponse response = new EntityResponse<>();

        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }
}
