package com.emtech.dairyapp.intergrations.mifugo.wallet;

import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerWalletService {

    private final FarmerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final FarmerRepo farmerRepo;

    public FarmerWallet getOrCreateWallet(String nationalId, Integer farmerNo, String name, String phone) {
        if (nationalId == null || nationalId.isBlank()) {
            throw new IllegalArgumentException("Farmer National ID is required for wallet.");
        }
        String cleanId = nationalId.trim();
        Optional<FarmerWallet> opt = walletRepository.findByFarmerNationalId(cleanId);
        if (opt.isPresent()) {
            return opt.get();
        }

        // Try to fetch farmer details from farmer repo if not supplied
        if ((name == null || name.isBlank()) && (farmerNo == null)) {
            Optional<Farmer> fOpt = farmerRepo.findByIdNumber(cleanId);
            if (fOpt.isPresent()) {
                Farmer f = fOpt.get();
                farmerNo = f.getFarmerNo();
                name = ((f.getFirstName() != null ? f.getFirstName() : "") + " " +
                        (f.getMiddleName() != null ? f.getMiddleName() + " " : "") +
                        (f.getLastName() != null ? f.getLastName() : "")).trim();
                phone = f.getMobileNo();
            }
        }

        FarmerWallet wallet = new FarmerWallet();
        wallet.setFarmerNationalId(cleanId);
        wallet.setFarmerNo(farmerNo);
        wallet.setFarmerName(name != null && !name.isBlank() ? name : "Farmer " + cleanId);
        wallet.setPhoneNumber(phone != null ? phone : "");
        wallet.setWalletAccountNumber("WAL-BHT-" + cleanId);
        wallet.setBalance(0.0);
        wallet.setTotalDisbursedClaims(0.0);
        wallet.setTotalMilkEarnings(0.0);
        wallet.setTotalWithdrawn(0.0);
        wallet.setStatus("ACTIVE");
        wallet.setCurrency("KES");
        wallet.setLastTransactionDate(new Date());

        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletTransaction creditClaimPayout(String nationalId, Integer farmerNo, String name, String phone,
                                                Double amount, String claimRef, String description,
                                                String approvedBy, String settledBy) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be greater than zero.");
        }

        FarmerWallet wallet = getOrCreateWallet(nationalId, farmerNo, name, phone);
        double prevBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
        double newBalance = prevBalance + amount;

        wallet.setBalance(Math.round(newBalance * 100.0) / 100.0);
        double totalClaims = (wallet.getTotalDisbursedClaims() != null ? wallet.getTotalDisbursedClaims() : 0.0) + amount;
        wallet.setTotalDisbursedClaims(Math.round(totalClaims * 100.0) / 100.0);
        wallet.setLastTransactionDate(new Date());
        walletRepository.save(wallet);

        String txRef = generateTxRef("CLM");
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionReference(txRef);
        tx.setWalletId(wallet.getId());
        tx.setFarmerNo(wallet.getFarmerNo());
        tx.setFarmerNationalId(nationalId);
        tx.setAmount(amount);
        tx.setTransactionType("CLAIM_INDEMNITY_PAYOUT");
        tx.setDirection("CREDIT");
        tx.setPreviousBalance(prevBalance);
        tx.setRunningBalance(wallet.getBalance());
        tx.setSourceReference(claimRef);
        tx.setDescription(description != null ? description : "Livestock insurance indemnity payout deposited into farmer wallet.");
        tx.setInitiatedBy(settledBy != null ? settledBy : "Old Mutual Claims Disbursal");
        tx.setApprovedBy(approvedBy != null ? approvedBy : "Underwriting Approver");
        tx.setChannel("INSURANCE_DIRECT_DISBURSEMENT");
        tx.setStatus("COMPLETED");

        return transactionRepository.save(tx);
    }

    @Transactional
    public WalletTransaction creditMilkPayment(String nationalId, Integer farmerNo, String name, String phone,
                                               Double amount, String sourceRef, String description) {
        FarmerWallet wallet = getOrCreateWallet(nationalId, farmerNo, name, phone);
        double prevBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
        double newBalance = prevBalance + amount;

        wallet.setBalance(Math.round(newBalance * 100.0) / 100.0);
        double totalMilk = (wallet.getTotalMilkEarnings() != null ? wallet.getTotalMilkEarnings() : 0.0) + amount;
        wallet.setTotalMilkEarnings(Math.round(totalMilk * 100.0) / 100.0);
        wallet.setLastTransactionDate(new Date());
        walletRepository.save(wallet);

        String txRef = generateTxRef("MLK");
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionReference(txRef);
        tx.setWalletId(wallet.getId());
        tx.setFarmerNo(wallet.getFarmerNo());
        tx.setFarmerNationalId(nationalId);
        tx.setAmount(amount);
        tx.setTransactionType("MILK_PAYMENT_CREDIT");
        tx.setDirection("CREDIT");
        tx.setPreviousBalance(prevBalance);
        tx.setRunningBalance(wallet.getBalance());
        tx.setSourceReference(sourceRef);
        tx.setDescription(description != null ? description : "Milk collection payment credited to farmer wallet.");
        tx.setInitiatedBy("Bahati Co-op Finance");
        tx.setApprovedBy("Finance Manager");
        tx.setChannel("COOPERATIVE_MILK_PAYOUT");
        tx.setStatus("COMPLETED");

        return transactionRepository.save(tx);
    }

    @Transactional
    public EntityResponse<WalletTransaction> debitWithdrawal(String nationalId, Double amount, String destinationPhone, String channel) {
        try {
            Optional<FarmerWallet> opt = walletRepository.findByFarmerNationalId(nationalId);
            if (opt.isEmpty()) {
                return new EntityResponse<>("Wallet not found for farmer: " + nationalId, HttpStatus.NOT_FOUND.value(), null);
            }
            FarmerWallet wallet = opt.get();
            if (amount == null || amount <= 0) {
                return new EntityResponse<>("Invalid withdrawal amount.", HttpStatus.BAD_REQUEST.value(), null);
            }
            if (wallet.getBalance() < amount) {
                return new EntityResponse<>("Insufficient wallet funds. Current balance: KES " + wallet.getBalance(), HttpStatus.BAD_REQUEST.value(), null);
            }

            double prevBalance = wallet.getBalance();
            double newBalance = prevBalance - amount;
            wallet.setBalance(Math.round(newBalance * 100.0) / 100.0);
            double totalWithdrawn = (wallet.getTotalWithdrawn() != null ? wallet.getTotalWithdrawn() : 0.0) + amount;
            wallet.setTotalWithdrawn(Math.round(totalWithdrawn * 100.0) / 100.0);
            wallet.setLastTransactionDate(new Date());
            walletRepository.save(wallet);

            String txRef = generateTxRef("WDR");
            WalletTransaction tx = new WalletTransaction();
            tx.setTransactionReference(txRef);
            tx.setWalletId(wallet.getId());
            tx.setFarmerNo(wallet.getFarmerNo());
            tx.setFarmerNationalId(nationalId);
            tx.setAmount(amount);
            tx.setTransactionType("MPESA_WITHDRAWAL");
            tx.setDirection("DEBIT");
            tx.setPreviousBalance(prevBalance);
            tx.setRunningBalance(wallet.getBalance());
            tx.setSourceReference(destinationPhone != null ? destinationPhone : wallet.getPhoneNumber());
            tx.setDescription("Disbursement withdrawal to M-Pesa account (" + (destinationPhone != null ? destinationPhone : wallet.getPhoneNumber()) + ")");
            tx.setInitiatedBy("Farmer Portal");
            tx.setApprovedBy("System Auto-Disburse");
            tx.setChannel(channel != null ? channel : "MPESA_B2C");
            tx.setStatus("COMPLETED");

            WalletTransaction savedTx = transactionRepository.save(tx);
            return new EntityResponse<>("Withdrawal of KES " + amount + " successful to " + tx.getSourceReference(), HttpStatus.OK.value(), savedTx);

        } catch (Exception e) {
            log.error("Error debiting withdrawal: ", e);
            return new EntityResponse<>("Failed to process withdrawal: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public EntityResponse<FarmerWallet> getWalletByNationalId(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            return new EntityResponse<>("National ID / Member No is required.", HttpStatus.BAD_REQUEST.value(), null);
        }
        String cleanId = nationalId.trim();
        Optional<FarmerWallet> opt = walletRepository.findByFarmerNationalId(cleanId);
        if (opt.isEmpty()) {
            try {
                int fn = Integer.parseInt(cleanId);
                opt = walletRepository.findByFarmerNo(fn);
            } catch (NumberFormatException ignored) {}
        }
        if (opt.isEmpty() && farmerRepo != null) {
            Optional<Farmer> fOpt = farmerRepo.findByIdNumber(cleanId);
            if (fOpt.isPresent() && fOpt.get().getFarmerNo() != null) {
                opt = walletRepository.findByFarmerNo(fOpt.get().getFarmerNo());
            }
        }
        if (opt.isPresent()) {
            return new EntityResponse<>("Wallet retrieved.", HttpStatus.OK.value(), opt.get());
        }
        // If not found, create active default wallet for farmer
        FarmerWallet wallet = getOrCreateWallet(cleanId, null, null, null);
        return new EntityResponse<>("Wallet created and retrieved.", HttpStatus.OK.value(), wallet);
    }

    public EntityResponse<List<WalletTransaction>> getTransactions(Long walletId) {
        List<WalletTransaction> list = transactionRepository.findByWalletIdOrderByTransactionDateDesc(walletId);
        return new EntityResponse<>("Transactions retrieved.", HttpStatus.OK.value(), list);
    }

    public EntityResponse<List<WalletTransaction>> getTransactionsByNationalId(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            return new EntityResponse<>("National ID / Member No required.", HttpStatus.OK.value(), Collections.emptyList());
        }
        String cleanId = nationalId.trim();
        Integer fNo = null;
        try {
            fNo = Integer.parseInt(cleanId);
        } catch (NumberFormatException ignored) {}
        if (fNo == null && farmerRepo != null) {
            Optional<Farmer> fOpt = farmerRepo.findByIdNumber(cleanId);
            if (fOpt.isPresent()) {
                fNo = fOpt.get().getFarmerNo();
            }
        }
        List<WalletTransaction> list;
        if (fNo != null) {
            list = transactionRepository.findByFarmerNationalIdOrFarmerNoOrderByTransactionDateDesc(cleanId, fNo);
        } else {
            list = transactionRepository.findByFarmerNationalIdOrderByTransactionDateDesc(cleanId);
        }
        return new EntityResponse<>("Transactions retrieved.", HttpStatus.OK.value(), list);
    }

    public EntityResponse<List<FarmerWallet>> getAllWallets() {
        List<FarmerWallet> list = walletRepository.findAll();
        return new EntityResponse<>("Wallets retrieved.", HttpStatus.OK.value(), list);
    }

    public EntityResponse<Map<String, Object>> getWalletStats() {
        try {
            long count = walletRepository.count();
            Double sumBalances = walletRepository.sumTotalWalletBalances();
            Double sumClaims = walletRepository.sumTotalDisbursedClaims();
            Double sumMilk = walletRepository.sumTotalMilkEarnings();

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalWallets", count);
            stats.put("totalWalletBalancesKes", sumBalances != null ? sumBalances : 0.0);
            stats.put("totalDisbursedClaimsKes", sumClaims != null ? sumClaims : 0.0);
            stats.put("totalMilkEarningsKes", sumMilk != null ? sumMilk : 0.0);

            return new EntityResponse<>("Wallet statistics retrieved.", HttpStatus.OK.value(), stats);
        } catch (Exception e) {
            log.error("Error computing wallet stats: ", e);
            return new EntityResponse<>("Failed to compute wallet stats: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyMap());
        }
    }

    private String generateTxRef(String prefix) {
        String dateCode = new SimpleDateFormat("yyyyMMdd").format(new Date());
        int randomCode = 1000 + new Random().nextInt(9000);
        return "WTX-" + prefix + "-" + dateCode + "-" + randomCode;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialWallets() {
        try {
            if (walletRepository.count() > 0) return;
            log.info("Seeding initial farmer wallets with historical payouts & milk proceeds...");

            // 1. James Mwangi Ndung'u (24891024) - Settled Claim for Daisy (BF-084) KES 126,000 + Milk proceeds
            FarmerWallet w1 = new FarmerWallet();
            w1.setWalletAccountNumber("WAL-BHT-24891024");
            w1.setFarmerNo(1042);
            w1.setFarmerNationalId("24891024");
            w1.setFarmerName("James Mwangi Ndung'u");
            w1.setPhoneNumber("0722123456");
            w1.setBalance(158400.0);
            w1.setTotalDisbursedClaims(126000.0);
            w1.setTotalMilkEarnings(62400.0);
            w1.setTotalWithdrawn(30000.0);
            w1.setStatus("ACTIVE");
            w1.setCurrency("KES");
            w1.setLastTransactionDate(new Date(System.currentTimeMillis() - 2L * 86400000));
            walletRepository.save(w1);

            // Transaction 1: Claim payout credit
            WalletTransaction tx1 = new WalletTransaction();
            tx1.setTransactionReference("WTX-CLM-20261001-4921");
            tx1.setWalletId(w1.getId());
            tx1.setFarmerNo(1042);
            tx1.setFarmerNationalId("24891024");
            tx1.setAmount(126000.0);
            tx1.setTransactionType("CLAIM_INDEMNITY_PAYOUT");
            tx1.setDirection("CREDIT");
            tx1.setPreviousBalance(32400.0);
            tx1.setRunningBalance(158400.0);
            tx1.setSourceReference("OM-CLM-20261001-4921");
            tx1.setDescription("Old Mutual Insurance indemnity payout for Dairy Cow Daisy (BF-084) - East Coast Fever mortality.");
            tx1.setInitiatedBy("finance_disbursal_om");
            tx1.setApprovedBy("underwriting_lead_karani");
            tx1.setChannel("INSURANCE_DIRECT_DISBURSEMENT");
            tx1.setStatus("COMPLETED");
            tx1.setTransactionDate(new Date(System.currentTimeMillis() - 2L * 86400000));
            transactionRepository.save(tx1);

            // Transaction 2: Milk payment credit
            WalletTransaction tx2 = new WalletTransaction();
            tx2.setTransactionReference("WTX-MLK-20260930-1102");
            tx2.setWalletId(w1.getId());
            tx2.setFarmerNo(1042);
            tx2.setFarmerNationalId("24891024");
            tx2.setAmount(32400.0);
            tx2.setTransactionType("MILK_PAYMENT_CREDIT");
            tx2.setDirection("CREDIT");
            tx2.setPreviousBalance(0.0);
            tx2.setRunningBalance(32400.0);
            tx2.setSourceReference("BATCH-SEP-2026-BHT");
            tx2.setDescription("September 2026 milk delivery cycle earnings (648 kg @ KES 50/kg).");
            tx2.setInitiatedBy("Bahati Co-op Finance");
            tx2.setApprovedBy("Finance Director");
            tx2.setChannel("COOPERATIVE_MILK_PAYOUT");
            tx2.setStatus("COMPLETED");
            tx2.setTransactionDate(new Date(System.currentTimeMillis() - 12L * 86400000));
            transactionRepository.save(tx2);

            // 2. Mary Muthoni Kamau (18273645) - Settled Claim for Queen (BF-167) KES 135,000
            FarmerWallet w2 = new FarmerWallet();
            w2.setWalletAccountNumber("WAL-BHT-18273645");
            w2.setFarmerNo(1012);
            w2.setFarmerNationalId("18273645");
            w2.setFarmerName("Mary Muthoni Kamau");
            w2.setPhoneNumber("0712348901");
            w2.setBalance(173500.0);
            w2.setTotalDisbursedClaims(135000.0);
            w2.setTotalMilkEarnings(58500.0);
            w2.setTotalWithdrawn(20000.0);
            w2.setStatus("ACTIVE");
            w2.setCurrency("KES");
            w2.setLastTransactionDate(new Date(System.currentTimeMillis() - 15L * 86400000));
            walletRepository.save(w2);

            WalletTransaction tx3 = new WalletTransaction();
            tx3.setTransactionReference("WTX-CLM-20260920-7744");
            tx3.setWalletId(w2.getId());
            tx3.setFarmerNo(1012);
            tx3.setFarmerNationalId("18273645");
            tx3.setAmount(135000.0);
            tx3.setTransactionType("CLAIM_INDEMNITY_PAYOUT");
            tx3.setDirection("CREDIT");
            tx3.setPreviousBalance(38500.0);
            tx3.setRunningBalance(173500.0);
            tx3.setSourceReference("OM-CLM-20260920-7744");
            tx3.setDescription("Old Mutual Insurance indemnity payout for Cow Queen (BF-167) - Hardware Disease mortality.");
            tx3.setInitiatedBy("finance_disbursal_om");
            tx3.setApprovedBy("underwriting_lead_karani");
            tx3.setChannel("INSURANCE_DIRECT_DISBURSEMENT");
            tx3.setStatus("COMPLETED");
            tx3.setTransactionDate(new Date(System.currentTimeMillis() - 15L * 86400000));
            transactionRepository.save(tx3);

            // 3. Grace Wanjiku Kariuki (19827364)
            FarmerWallet w3 = new FarmerWallet();
            w3.setWalletAccountNumber("WAL-BHT-19827364");
            w3.setFarmerNo(1088);
            w3.setFarmerNationalId("19827364");
            w3.setFarmerName("Grace Wanjiku Kariuki");
            w3.setPhoneNumber("0711987654");
            w3.setBalance(42800.0);
            w3.setTotalDisbursedClaims(0.0); // Bessie's claim is APPROVED, pending disbursal!
            w3.setTotalMilkEarnings(42800.0);
            w3.setTotalWithdrawn(0.0);
            w3.setStatus("ACTIVE");
            w3.setCurrency("KES");
            w3.setLastTransactionDate(new Date(System.currentTimeMillis() - 5L * 86400000));
            walletRepository.save(w3);

            // 4. Peter Kiprono Koech (30192847)
            FarmerWallet w4 = new FarmerWallet();
            w4.setWalletAccountNumber("WAL-BHT-30192847");
            w4.setFarmerNo(1115);
            w4.setFarmerNationalId("30192847");
            w4.setFarmerName("Peter Kiprono Koech");
            w4.setPhoneNumber("0700543210");
            w4.setBalance(29500.0);
            w4.setTotalDisbursedClaims(0.0);
            w4.setTotalMilkEarnings(29500.0);
            w4.setTotalWithdrawn(0.0);
            w4.setStatus("ACTIVE");
            w4.setCurrency("KES");
            w4.setLastTransactionDate(new Date());
            walletRepository.save(w4);

            log.info("Successfully seeded farmer wallets and transaction ledgers.");
        } catch (Exception e) {
            log.error("Error seeding wallets: ", e);
        }
    }
}

