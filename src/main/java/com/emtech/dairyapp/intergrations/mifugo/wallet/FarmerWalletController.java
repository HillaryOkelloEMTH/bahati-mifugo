package com.emtech.dairyapp.intergrations.mifugo.wallet;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class FarmerWalletController {

    private final FarmerWalletService walletService;

    @GetMapping
    public ResponseEntity<EntityResponse<List<FarmerWallet>>> getAllWallets() {
        EntityResponse<List<FarmerWallet>> resp = walletService.getAllWallets();
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("farmer/{nationalId}")
    public ResponseEntity<EntityResponse<FarmerWallet>> getWalletByNationalId(@PathVariable String nationalId) {
        EntityResponse<FarmerWallet> resp = walletService.getWalletByNationalId(nationalId);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("stats")
    public ResponseEntity<EntityResponse<Map<String, Object>>> getWalletStats() {
        EntityResponse<Map<String, Object>> resp = walletService.getWalletStats();
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("{walletId}/transactions")
    public ResponseEntity<EntityResponse<List<WalletTransaction>>> getTransactions(@PathVariable Long walletId) {
        EntityResponse<List<WalletTransaction>> resp = walletService.getTransactions(walletId);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @GetMapping("farmer/{nationalId}/transactions")
    public ResponseEntity<EntityResponse<List<WalletTransaction>>> getTransactionsByNationalId(@PathVariable String nationalId) {
        EntityResponse<List<WalletTransaction>> resp = walletService.getTransactionsByNationalId(nationalId);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PostMapping("farmer/{nationalId}/withdraw")
    public ResponseEntity<EntityResponse<WalletTransaction>> withdraw(
            @PathVariable String nationalId,
            @RequestBody Map<String, Object> body) {
        Double amount = body.get("amount") != null ? Double.parseDouble(String.valueOf(body.get("amount"))) : 0.0;
        String phone = body.get("phone") != null ? String.valueOf(body.get("phone")) : null;
        String channel = body.get("channel") != null ? String.valueOf(body.get("channel")) : "MPESA_B2C";

        EntityResponse<WalletTransaction> resp = walletService.debitWithdrawal(nationalId, amount, phone, channel);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }
}
