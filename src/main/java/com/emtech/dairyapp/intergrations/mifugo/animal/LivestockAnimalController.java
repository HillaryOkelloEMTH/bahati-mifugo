package com.emtech.dairyapp.intergrations.mifugo.animal;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/animals")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class LivestockAnimalController {

    private final LivestockAnimalService animalService;

    @GetMapping
    public ResponseEntity<EntityResponse<List<LivestockAnimal>>> getAllAnimals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String search) {
        EntityResponse<List<LivestockAnimal>> resp = animalService.getAllAnimals(status, breed, search);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("{tag}")
    public ResponseEntity<EntityResponse<LivestockAnimal>> getAnimalByTag(@PathVariable String tag) {
        EntityResponse<LivestockAnimal> resp = animalService.getAnimalByTag(tag);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("by-muzzle/{muzzleKey}")
    public ResponseEntity<EntityResponse<LivestockAnimal>> getAnimalByMuzzle(@PathVariable String muzzleKey) {
        EntityResponse<LivestockAnimal> resp = animalService.getAnimalByMuzzle(muzzleKey);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("{tag}/vaccinations")
    public ResponseEntity<EntityResponse<List<LivestockVaccination>>> getVaccinations(@PathVariable String tag) {
        EntityResponse<List<LivestockVaccination>> resp = animalService.getVaccinations(tag);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("{tag}/full-history")
    public ResponseEntity<EntityResponse<AnimalFullHistoryDto>> getFullHistory(@PathVariable String tag) {
        EntityResponse<AnimalFullHistoryDto> resp = animalService.getFullHistory(tag);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("farmer/{nationalId}")
    public ResponseEntity<EntityResponse<List<LivestockAnimal>>> getAnimalsByFarmer(@PathVariable String nationalId) {
        EntityResponse<List<LivestockAnimal>> resp = animalService.getAnimalsByFarmer(nationalId);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("stats")
    public ResponseEntity<EntityResponse<CoopHerdMetricsDto>> getCoopHerdMetrics() {
        EntityResponse<CoopHerdMetricsDto> resp = animalService.getCoopHerdMetrics();
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PostMapping("{tag}/vaccinations")
    public ResponseEntity<EntityResponse<LivestockVaccination>> addVaccination(
            @PathVariable String tag,
            @RequestBody LivestockVaccination vaccination) {
        EntityResponse<LivestockVaccination> resp = animalService.addVaccination(tag, vaccination);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PostMapping("{tag}/health-records")
    public ResponseEntity<EntityResponse<LivestockHealthRecord>> addHealthRecord(
            @PathVariable String tag,
            @RequestBody LivestockHealthRecord record) {
        EntityResponse<LivestockHealthRecord> resp = animalService.addHealthRecord(tag, record);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PutMapping("{tag}/record-death")
    public ResponseEntity<EntityResponse<LivestockAnimal>> recordDeath(
            @PathVariable String tag,
            @RequestBody Map<String, String> body) {
        String cause = body.get("causeOfDeath");
        String date = body.get("dateOfDeath");
        String disposal = body.get("carcassDisposalMethod");
        String vetName = body.get("attendingVetName");
        String vetKvb = body.get("attendingVetKvb");
        String notes = body.get("postMortemNotes");

        EntityResponse<LivestockAnimal> resp = animalService.recordDeath(tag, cause, date, disposal, vetName, vetKvb, notes);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PostMapping("{tag}/muzzle-verify")
    public ResponseEntity<EntityResponse<Map<String, Object>>> verifyMuzzleScan(
            @PathVariable String tag,
            @RequestBody(required = false) Map<String, Object> body) {
        EntityResponse<Map<String, Object>> resp = animalService.verifyMuzzle(tag, body);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }
}

