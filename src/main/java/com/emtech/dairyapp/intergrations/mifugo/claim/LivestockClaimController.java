package com.emtech.dairyapp.intergrations.mifugo.claim;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/insurance/claims")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class LivestockClaimController {

    private final LivestockClaimService claimService;

    @PostMapping
    public ResponseEntity<EntityResponse<LivestockClaim>> lodgeClaim(@RequestBody LodgeClaimDto dto) {
        EntityResponse<LivestockClaim> resp = claimService.lodgeClaim(dto);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping
    public ResponseEntity<EntityResponse<List<LivestockClaim>>> getAllClaims(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String nationalId) {
        EntityResponse<List<LivestockClaim>> resp = claimService.getAllClaims(status, search, nationalId);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("{id}")
    public ResponseEntity<EntityResponse<LivestockClaim>> getClaimById(@PathVariable Long id) {
        EntityResponse<LivestockClaim> resp = claimService.getClaimById(id);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("reference/{reference}")
    public ResponseEntity<EntityResponse<LivestockClaim>> getClaimByReference(@PathVariable String reference) {
        EntityResponse<LivestockClaim> resp = claimService.getClaimByReference(reference);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PutMapping("{id}/vet-assessment")
    public ResponseEntity<EntityResponse<LivestockClaim>> recordVetAssessment(
            @PathVariable Long id,
            @RequestBody VetAssessmentDto dto) {
        EntityResponse<LivestockClaim> resp = claimService.recordVetAssessment(id, dto);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PostMapping("{id}/verify-biometrics")
    public ResponseEntity<EntityResponse<LivestockClaim>> verifyBiometrics(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String postMortemKey = body != null ? body.get("postMortemMuzzleKey") : null;
        EntityResponse<LivestockClaim> resp = claimService.verifyBiometrics(id, postMortemKey);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PutMapping("{id}/adjudicate")
    public ResponseEntity<EntityResponse<LivestockClaim>> adjudicateClaim(
            @PathVariable Long id,
            @RequestBody AdjudicateClaimDto dto) {
        EntityResponse<LivestockClaim> resp = claimService.adjudicateClaim(id, dto);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PutMapping("{id}/settle")
    public ResponseEntity<EntityResponse<LivestockClaim>> settleClaim(
            @PathVariable Long id,
            @RequestBody SettleClaimDto dto) {
        EntityResponse<LivestockClaim> resp = claimService.settleClaim(id, dto);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("stats")
    public ResponseEntity<EntityResponse<ClaimsDashboardMetrics>> getClaimsStats() {
        EntityResponse<ClaimsDashboardMetrics> resp = claimService.getClaimsStats();
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }
}

