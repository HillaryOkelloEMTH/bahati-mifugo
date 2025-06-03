package com.emtech.dairyapp.Dairy.Supply.quality;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/quality")
@RequiredArgsConstructor
public class QualityTestsController {
    private QualityService service;

    @PostMapping("post-farmer-agent")
    public ResponseEntity<?> postFarmer(@RequestParam Integer farmerNo) {
        var res = service.postFarmer(farmerNo);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    @GetMapping("get/test-logs")
    public ResponseEntity<?> getTestLogs() {
        var res = service.getTestsLogs();
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }
}
