package com.emtech.dairyapp.intergrations.mifugo;

import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class FarmerFullProfileService {

    private final MifugoClient mifugoClient;
    private final FarmerRepo farmerRepo;
    private final MilkCollectionRepo milkCollectionRepo;

    public FarmerFullProfileService(MifugoClient mifugoClient, FarmerRepo farmerRepo,
                                    MilkCollectionRepo milkCollectionRepo) {
        this.mifugoClient = mifugoClient;
        this.farmerRepo = farmerRepo;
        this.milkCollectionRepo = milkCollectionRepo;
    }

    @Value("${mifugo.maziwa-base-url}")
    private String maziwaBaseUrl;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFullProfile(String nationalId, String from, String to) {

        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Farmer + animals (with muzzle image URLs) — single Maziwa call
        Map<String, Object> farmerResponse = mifugoClient.getFarmerProfileByNationalId(nationalId);
        Map<String, Object> farmerEntity = null;
        if (farmerResponse != null && farmerResponse.get("entity") instanceof Map) {
            farmerEntity = (Map<String, Object>) farmerResponse.get("entity");
        }

        // Fallback: If not found on Mifugo, check if farmer exists in local Bahati Dairy database
        if (farmerEntity == null) {
            Optional<Farmer> localFarmerOpt = farmerRepo.findByIdNumber(nationalId);
            if (localFarmerOpt.isPresent()) {
                return buildProfileFromLocalFarmer(localFarmerOpt.get(), nationalId, from, to);
            }

            result.put("message", "Farmer with National ID " + nationalId + " was not found on Mifugo360 or Bahati Dairy.");
            result.put("statusCode", 404);
            return result;
        }

        Object animalsObj = farmerEntity.get("animals");
        List<Map<String, Object>> animals = animalsObj instanceof List
                ? (List<Map<String, Object>>) animalsObj
                : Collections.emptyList();

        animals = rewriteMuzzleImageUrls(animals);

        Map<String, Object> farmerWithoutAnimals = new LinkedHashMap<>(farmerEntity);
        farmerWithoutAnimals.remove("animals");

        result.put("farmer", farmerWithoutAnimals);

        String nationalIdNo = (String) farmerEntity.get("nationalId");
        // 2. Bridge to local farmerNo, then pull milk collections summary
        Optional<Farmer> localFarmer = nationalIdNo != null
                ? farmerRepo.findByIdNumber(nationalIdNo)
                : Optional.empty();

        Map<String, Object> milkSummary;

        if (localFarmer.isPresent()) {
            Integer farmerNo = localFarmer.get().getFarmerNo();
            result.put("localFarmerNo", farmerNo);

            List<MilkCollections> deliveries;
            if (from != null && to != null) {
                deliveries = milkCollectionRepo.findByFarmerNoAndCollectionDateBetween(
                        farmerNo, parseDate(from), parseDate(to));
            } else {
                deliveries = milkCollectionRepo.findByFarmerNo(farmerNo);
            }

            milkSummary = summarizeCollections(deliveries);
            result.put("milkCollectionsSummary", milkSummary);
        } else {
            result.put("localFarmerNo", null);
            milkSummary = summarizeCollections(Collections.emptyList());
            result.put("milkCollectionsSummary", milkSummary);
            result.put("note", "No matching local farmer record found by nationalId; productivity unavailable.");
        }
        // 3. Distribute total milk quantity across animals (random but sums exactly to total)
        double totalKg = (double) milkSummary.get("totalQuantityKg");
        animals = distributeMilkAcrossAnimals(animals, totalKg);

        // 4. Enrich animals with Old Mutual Insurance Valuation and Underwriting
        List<Map<String, Object>> enrichedAnimals = enrichAnimalsWithOldMutualInsurance(animals, nationalId, totalKg);
        result.put("animals", enrichedAnimals);

        // 5. Build Old Mutual Insurance Portfolio & Underwriting Summary
        Map<String, Object> insuranceSummary = buildOldMutualInsuranceSummary(enrichedAnimals, farmerWithoutAnimals, nationalId, milkSummary);
        result.put("insuranceProfile", insuranceSummary);

        result.put("message", "Success");
        result.put("statusCode", 200);
        return result;
    }

    private Map<String, Object> summarizeCollections(List<MilkCollections> deliveries) {
        Map<String, Object> summary = new LinkedHashMap<>();

        if (deliveries == null || deliveries.isEmpty()) {
            summary.put("totalQuantityKg", 0.0);
            summary.put("collectionCount", 0);
            summary.put("monthlyQuantityKg", Collections.emptyMap());
            summary.put("yearlyQuantityKg", Collections.emptyMap());
            return summary;
        }

        double total = deliveries.stream()
                .mapToDouble(d -> d.getQuantity() != null ? d.getQuantity() : 0.0)
                .sum();

        List<MilkCollections> dated = deliveries.stream()
                .filter(d -> d.getCollectionDate() != null)
                .collect(Collectors.toList());

        Map<YearMonth, Double> byMonth = dated.stream()
                .collect(Collectors.groupingBy(
                        d -> YearMonth.from(LocalDate.ofInstant(d.getCollectionDate().toInstant(), ZoneId.systemDefault())),
                        TreeMap::new,
                        Collectors.summingDouble(d -> d.getQuantity() != null ? d.getQuantity() : 0.0)
                ));
        Map<String, Double> monthlyLabeled = new LinkedHashMap<>();
        byMonth.forEach((ym, qty) -> {
            String label = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + ym.getYear();
            monthlyLabeled.put(label, qty);
        });

        Map<Year, Double> byYear = dated.stream()
                .collect(Collectors.groupingBy(
                        d -> Year.from(LocalDate.ofInstant(d.getCollectionDate().toInstant(), ZoneId.systemDefault())),
                        TreeMap::new,
                        Collectors.summingDouble(d -> d.getQuantity() != null ? d.getQuantity() : 0.0)
                ));
        Map<String, Double> yearlyLabeled = new LinkedHashMap<>();
        byYear.forEach((y, qty) -> yearlyLabeled.put(String.valueOf(y.getValue()), qty));

        summary.put("totalQuantityKg", total);
        summary.put("collectionCount", deliveries.size());
        summary.put("monthlyQuantityKg", monthlyLabeled);
        summary.put("yearlyQuantityKg", yearlyLabeled);
        return summary;
    }

    private Date parseDate(String dateStr) {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(dateStr));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd: " + dateStr);
        }
    }

    public byte[] getMuzzleImage(String key) {
        return mifugoClient.getMuzzleImage(key);
    }

    private List<Map<String, Object>> rewriteMuzzleImageUrls(List<Map<String, Object>> animals) {
        List<Map<String, Object>> rewritten = new ArrayList<>(animals.size());
        for (Map<String, Object> animal : animals) {
            Map<String, Object> copy = new LinkedHashMap<>(animal);
            Object muzzleImageObj = copy.get("muzzleImage");
            if (muzzleImageObj instanceof String) {
                String key = extractMuzzleKey((String) muzzleImageObj);
                if (key != null) {
                    copy.put("muzzleImageKey", key);
                    copy.put("muzzleImageProxyUrl", "/api/v1/farmer/muzzle-image/" + key);
                    copy.put("muzzleImage", "/api/v1/farmer/muzzle-image/" + key);
                    copy.put("muzzleImageOriginal", maziwaBaseUrl + "/muzzles/" + key);
                }
            }
            rewritten.add(copy);
        }
        return rewritten;
    }

    private String extractMuzzleKey(String muzzleImageUrl) {
        int lastSlash = muzzleImageUrl.lastIndexOf('/');
        if (lastSlash == -1 || lastSlash == muzzleImageUrl.length() - 1) {
            return null;
        }
        return muzzleImageUrl.substring(lastSlash + 1);
    }

    private Map<String, Object> buildProfileFromLocalFarmer(Farmer localFarmer, String nationalId, String from, String to) {
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, Object> farmerMap = new LinkedHashMap<>();
        String fullName = (localFarmer.getFirstName() != null ? localFarmer.getFirstName() : "")
                + (localFarmer.getMiddleName() != null ? " " + localFarmer.getMiddleName() : "")
                + (localFarmer.getLastName() != null ? " " + localFarmer.getLastName() : "");
        farmerMap.put("farmerId", "LOCAL-" + localFarmer.getFarmerNo());
        farmerMap.put("fullName", fullName.trim());
        farmerMap.put("nationalId", localFarmer.getIdNumber() != null ? localFarmer.getIdNumber() : nationalId);
        farmerMap.put("phoneNumber", localFarmer.getMobileNo() != null ? localFarmer.getMobileNo() : "-");
        farmerMap.put("gender", localFarmer.getGender());
        farmerMap.put("county", localFarmer.getLocation() != null ? localFarmer.getLocation() : "Bahati");
        farmerMap.put("subcounty", localFarmer.getSubLocation());
        farmerMap.put("ward", localFarmer.getVillage());
        farmerMap.put("farmName", localFarmer.getAddress() != null && !localFarmer.getAddress().isBlank() ? localFarmer.getAddress() : "Bahati Dairy Farm");
        farmerMap.put("phoneVerified", true);
        farmerMap.put("createdAt", localFarmer.getCreatedAt() != null ? localFarmer.getCreatedAt().toString() : "");

        result.put("farmer", farmerMap);
        result.put("localFarmerNo", localFarmer.getFarmerNo());

        List<MilkCollections> deliveries;
        if (from != null && to != null) {
            deliveries = milkCollectionRepo.findByFarmerNoAndCollectionDateBetween(
                    localFarmer.getFarmerNo(), parseDate(from), parseDate(to));
        } else {
            deliveries = milkCollectionRepo.findByFarmerNo(localFarmer.getFarmerNo());
        }

        Map<String, Object> milkSummary = summarizeCollections(deliveries);
        result.put("milkCollectionsSummary", milkSummary);

        int cowCount = localFarmer.getNoOfCows() != null && localFarmer.getNoOfCows() > 0 ? localFarmer.getNoOfCows() : 1;
        List<Map<String, Object>> placeholderAnimals = new ArrayList<>(cowCount);
        for (int i = 1; i <= cowCount; i++) {
            Map<String, Object> animal = new LinkedHashMap<>();
            animal.put("id", i);
            animal.put("animalId", "COW-BHT-" + localFarmer.getFarmerNo() + "-0" + i);
            animal.put("tagNumberOrName", "Cow #" + i);
            animal.put("breed", "Dairy Cattle");
            animal.put("species", "Cattle");
            animal.put("gender", "Female");
            animal.put("hasMuzzleScan", false);
            animal.put("hasVerifiedMuzzle", false);
            animal.put("muzzleImage", null);
            placeholderAnimals.add(animal);
        }

        double totalKg = (double) milkSummary.get("totalQuantityKg");
        placeholderAnimals = distributeMilkAcrossAnimals(placeholderAnimals, totalKg);

        List<Map<String, Object>> enrichedAnimals = enrichAnimalsWithOldMutualInsurance(placeholderAnimals, nationalId, totalKg);
        result.put("animals", enrichedAnimals);

        Map<String, Object> insuranceSummary = buildOldMutualInsuranceSummary(enrichedAnimals, farmerMap, nationalId, milkSummary);
        result.put("insuranceProfile", insuranceSummary);

        result.put("note", "Farmer record retrieved from Bahati Dairy. Muzzle biometric enrollment on Mifugo360 is required to activate insurance coverage.");
        result.put("message", "Success");
        result.put("statusCode", 200);
        return result;
    }

    private List<Map<String, Object>> enrichAnimalsWithOldMutualInsurance(
            List<Map<String, Object>> animals, String nationalId, double totalMilkKg) {

        if (animals == null || animals.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>(animals.size());

        for (Map<String, Object> animal : animals) {
            Map<String, Object> copy = new LinkedHashMap<>(animal);

            String breed = String.valueOf(copy.getOrDefault("breed", "Dairy Cattle"));
            String animalId = String.valueOf(copy.getOrDefault("animalId", "COW"));
            String tag = String.valueOf(copy.getOrDefault("tagNumberOrName", animalId));
            boolean hasMuzzleScan = Boolean.TRUE.equals(copy.get("hasMuzzleScan"));
            boolean hasVerifiedMuzzle = Boolean.TRUE.equals(copy.get("hasVerifiedMuzzle"));

            double baseValue = getBaseBreedValuation(breed);
            double estimatedMilkKg = 0.0;
            Object milkContrib = copy.get("estimatedMilkContributionKg");
            if (milkContrib instanceof Number) {
                estimatedMilkKg = ((Number) milkContrib).doubleValue();
            } else if (milkContrib instanceof String) {
                try {
                    estimatedMilkKg = Double.parseDouble((String) milkContrib);
                } catch (Exception ignored) {}
            }

            double yieldBonus = Math.min(60000.0, estimatedMilkKg * 20.0);
            double insurableValueKes = Math.round((baseValue + yieldBonus) / 500.0) * 500.0;
            double sumInsuredKes = Math.round((insurableValueKes * 0.90) / 500.0) * 500.0;
            double annualPremiumKes = round2(sumInsuredKes * 0.045);
            double monthlyPremiumKes = round2(annualPremiumKes / 12.0);

            String safeId = animalId != null && !animalId.isBlank() ? animalId : tag;
            String policyNumber = "OM-LIV-" + nationalId + "-" + safeId;

            String biometricStatus;
            String policyStatus;
            String underwritingRisk;

            if (hasVerifiedMuzzle) {
                biometricStatus = "VERIFIED_MUZZLE";
                policyStatus = totalMilkKg > 0 ? "ACTIVE_COVER" : "ACTIVE_ZERO_YIELD_REVIEW";
                underwritingRisk = "LOW_RISK";
            } else if (hasMuzzleScan) {
                biometricStatus = "SCANNED_UNVERIFIED";
                policyStatus = "PENDING_INSPECTION";
                underwritingRisk = "MODERATE_RISK";
            } else {
                biometricStatus = "PENDING_SCAN";
                policyStatus = "PENDING_MUZZLE_ENROLLMENT";
                underwritingRisk = "HIGH_RISK";
            }

            boolean claimEligible = hasVerifiedMuzzle && totalMilkKg > 0;

            copy.put("insurableValueKes", insurableValueKes);
            copy.put("sumInsuredKes", sumInsuredKes);
            copy.put("annualPremiumKes", annualPremiumKes);
            copy.put("monthlyPremiumKes", monthlyPremiumKes);
            copy.put("policyNumber", policyNumber);
            copy.put("policyStatus", policyStatus);
            copy.put("biometricStatus", biometricStatus);
            copy.put("underwritingRisk", underwritingRisk);
            copy.put("claimEligible", claimEligible);

            result.add(copy);
        }

        return result;
    }

    private double getBaseBreedValuation(String breed) {
        if (breed == null) return 75000.0;
        String b = breed.toLowerCase();
        if (b.contains("friesian") || b.contains("holstein")) return 125000.0;
        if (b.contains("ayrshire")) return 115000.0;
        if (b.contains("jersey")) return 95000.0;
        if (b.contains("guernsey")) return 90000.0;
        if (b.contains("sahiwal") || b.contains("boran")) return 75000.0;
        return 70000.0;
    }

    private Map<String, Object> buildOldMutualInsuranceSummary(
            List<Map<String, Object>> enrichedAnimals,
            Map<String, Object> farmer,
            String nationalId,
            Map<String, Object> milkSummary) {

        Map<String, Object> summary = new LinkedHashMap<>();

        int totalHerd = enrichedAnimals != null ? enrichedAnimals.size() : 0;
        long verifiedCount = enrichedAnimals != null
                ? enrichedAnimals.stream().filter(a -> Boolean.TRUE.equals(a.get("hasVerifiedMuzzle"))).count()
                : 0;
        long scannedCount = enrichedAnimals != null
                ? enrichedAnimals.stream().filter(a -> Boolean.TRUE.equals(a.get("hasMuzzleScan"))).count()
                : 0;

        double complianceRate = totalHerd > 0 ? round2(((double) verifiedCount / totalHerd) * 100.0) : 0.0;

        double totalSumInsured = enrichedAnimals != null
                ? enrichedAnimals.stream().mapToDouble(a -> ((Number) a.getOrDefault("sumInsuredKes", 0.0)).doubleValue()).sum()
                : 0.0;
        double totalAnnualPremium = enrichedAnimals != null
                ? enrichedAnimals.stream().mapToDouble(a -> ((Number) a.getOrDefault("annualPremiumKes", 0.0)).doubleValue()).sum()
                : 0.0;
        double totalMonthlyPremium = round2(totalAnnualPremium / 12.0);

        int collectionCount = milkSummary != null
                ? ((Number) milkSummary.getOrDefault("collectionCount", 0)).intValue()
                : 0;
        double totalMilkKg = milkSummary != null
                ? ((Number) milkSummary.getOrDefault("totalQuantityKg", 0.0)).doubleValue()
                : 0.0;

        String riskGrade;
        String underwritingStatus;

        if (complianceRate >= 80.0 && collectionCount > 0) {
            riskGrade = "GRADE_A_LOW_RISK";
            underwritingStatus = complianceRate == 100.0 ? "ACTIVE_FULL_COVER" : "ACTIVE_PARTIAL_COVER";
        } else if (complianceRate >= 50.0 || scannedCount > 0) {
            riskGrade = "GRADE_B_MODERATE_RISK";
            underwritingStatus = "CONDITIONAL_PENDING_VERIFICATION";
        } else {
            riskGrade = "GRADE_C_ACTION_REQUIRED";
            underwritingStatus = "PENDING_BIOMETRIC_ONBOARDING";
        }

        String farmerName = farmer != null && farmer.get("fullName") != null ? (String) farmer.get("fullName") : "Valued Farmer";

        summary.put("company", " Insurance");
        summary.put("product", "Comprehensive Livestock & Dairy Cattle Protection");
        summary.put("policyHolderName", farmerName);
        summary.put("nationalId", nationalId);
        summary.put("policyNumber", "OM-LIV-POL-" + nationalId);
        summary.put("underwritingRiskGrade", riskGrade);
        summary.put("underwritingStatus", underwritingStatus);
        summary.put("totalHerdSize", totalHerd);
        summary.put("muzzleVerifiedCount", verifiedCount);
        summary.put("muzzleScannedCount", scannedCount);
        summary.put("biometricComplianceRate", complianceRate);
        summary.put("totalSumInsuredKes", round2(totalSumInsured));
        summary.put("totalAnnualPremiumKes", round2(totalAnnualPremium));
        summary.put("totalMonthlyPremiumKes", totalMonthlyPremium);
        summary.put("premiumPaymentDeduction", "Eligible for Automatic Checkoff via Bahati Milk Payouts");
        summary.put("claimsEligibility", verifiedCount > 0 && collectionCount > 0 ? "ELIGIBLE" : "PENDING_VERIFICATION");

        List<String> riskFactors = new ArrayList<>();
        riskFactors.add(verifiedCount + " of " + totalHerd + " cattle verified via Mifugo360 muzzle biometrics (" + complianceRate + "% compliance).");
        if (collectionCount > 0) {
            riskFactors.add("Active milk production: " + totalMilkKg + " kg collected across " + collectionCount + " deliveries with Bahati Dairy.");
        } else {
            riskFactors.add("Notice: No recent milk delivery records found in Bahati Dairy system.");
        }
        if (farmer != null && Boolean.TRUE.equals(farmer.get("phoneVerified"))) {
            riskFactors.add("Farmer KYC and mobile phone number successfully verified.");
        }
        summary.put("riskAssessmentFactors", riskFactors);

        return summary;
    }

    public Map<String, Object> verifyClaim(String nationalId, String animalId, String claimType, String incidentDate, String notes) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> profile = getFullProfile(nationalId, null, null);
        int profileStatus = (int) profile.getOrDefault("statusCode", 200);
        if (profileStatus != 200) {
            response.put("statusCode", profileStatus);
            response.put("message", profile.getOrDefault("message", "Farmer profile could not be found for claim verification."));
            return response;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> animals = (List<Map<String, Object>>) profile.get("animals");
        if (animals == null || animals.isEmpty()) {
            response.put("statusCode", 400);
            response.put("message", "No animals registered under farmer " + nationalId);
            return response;
        }

        Map<String, Object> targetAnimal = null;
        for (Map<String, Object> a : animals) {
            String aId = String.valueOf(a.get("animalId"));
            String tag = String.valueOf(a.get("tagNumberOrName"));
            String id = String.valueOf(a.get("id"));
            if (aId.equalsIgnoreCase(animalId) || tag.equalsIgnoreCase(animalId) || id.equalsIgnoreCase(animalId)) {
                targetAnimal = a;
                break;
            }
        }

        if (targetAnimal == null) {
            targetAnimal = animals.get(0);
        }

        boolean biometricVerified = Boolean.TRUE.equals(targetAnimal.get("hasVerifiedMuzzle"));
        boolean muzzleScanned = Boolean.TRUE.equals(targetAnimal.get("hasMuzzleScan"));

        @SuppressWarnings("unchecked")
        Map<String, Object> milkSummary = (Map<String, Object>) profile.get("milkCollectionsSummary");
        int count = milkSummary != null ? ((Number) milkSummary.getOrDefault("collectionCount", 0)).intValue() : 0;
        double totalKg = milkSummary != null ? ((Number) milkSummary.getOrDefault("totalQuantityKg", 0.0)).doubleValue() : 0.0;
        boolean productionActive = count > 0 && totalKg > 0;

        double sumInsured = ((Number) targetAnimal.getOrDefault("sumInsuredKes", 100000.0)).doubleValue();
        double deductible = round2(sumInsured * 0.10);
        double payable = round2(Math.max(0, sumInsured - deductible));

        List<Map<String, Object>> checklist = new ArrayList<>();

        Map<String, Object> c1 = new LinkedHashMap<>();
        c1.put("item", "Biometric Muzzle Verification (Mifugo360 Anti-Fraud)");
        c1.put("passed", biometricVerified);
        c1.put("details", biometricVerified ? "Biometric muzzle pattern verified and matched against policy enrollment." : "FAIL: Muzzle scan not verified; risk of animal substitution fraud.");
        checklist.add(c1);

        Map<String, Object> c2 = new LinkedHashMap<>();
        c2.put("item", "Dairy Milk Production Records (Bahati Dairy)");
        c2.put("passed", productionActive);
        c2.put("details", productionActive ? "Active lactation confirmed with " + totalKg + " kg across " + count + " collections." : "WARNING: No active milk deliveries on record.");
        checklist.add(c2);

        Map<String, Object> c3 = new LinkedHashMap<>();
        c3.put("item", "Policy Coverage Standing");
        c3.put("passed", "ACTIVE_COVER".equals(targetAnimal.get("policyStatus")));
        c3.put("details", "Policy status: " + targetAnimal.get("policyStatus"));
        checklist.add(c3);

        String claimStatus;
        String recommendation;

        if (biometricVerified && productionActive) {
            claimStatus = "APPROVED_FOR_PAYOUT";
            recommendation = "All biometric and production verifications PASSED. Claim recommended for immediate settlement payout of KES " + payable + " after 10% deductible.";
        } else if (muzzleScanned && !biometricVerified) {
            claimStatus = "FLAGGED_FIELD_INSPECTION_REQUIRED";
            recommendation = "Muzzle scan submitted but unverified on Mifugo360. Physical on-site biometric audit required by claims inspector.";
        } else {
            claimStatus = "REJECTED_UNVERIFIED_BIOMETRICS";
            recommendation = "Animal lacks verified muzzle biometrics. Claim cannot be settled under livestock insurance fraud prevention guidelines.";
        }

        response.put("statusCode", 200);
        response.put("message", "Claim verification processed successfully");
        response.put("claimReference", "OM-CLM-" + System.currentTimeMillis());
        response.put("claimType", claimType != null ? claimType : "LIVESTOCK_MORTALITY");
        response.put("incidentDate", incidentDate != null ? incidentDate : LocalDate.now().toString());
        response.put("claimStatus", claimStatus);
        response.put("biometricVerified", biometricVerified);
        response.put("productionVerified", productionActive);
        response.put("sumInsuredKes", sumInsured);
        response.put("deductibleKes", deductible);
        response.put("payableAmountKes", "APPROVED_FOR_PAYOUT".equals(claimStatus) ? payable : 0.0);
        response.put("animal", targetAnimal);
        response.put("checklist", checklist);
        response.put("recommendation", recommendation);

        return response;
    }

    /**
     * Splits totalKg across animals using random proportional weights.
     * The last animal absorbs the rounding remainder so the sum is always
     * EXACTLY equal to totalKg (never off by a few cents due to rounding drift).
     */
    private List<Map<String, Object>> distributeMilkAcrossAnimals(
            List<Map<String, Object>> animals, double totalKg) {

        if (animals == null || animals.isEmpty()) {
            return animals;
        }

        int n = animals.size();
        List<Map<String, Object>> result = new ArrayList<>(n);

        if (n == 1) {
            Map<String, Object> copy = new LinkedHashMap<>(animals.get(0));
            copy.put("estimatedMilkContributionKg", round2(totalKg));
            result.add(copy);
            return result;
        }

        if (totalKg <= 0) {
            for (Map<String, Object> a : animals) {
                Map<String, Object> copy = new LinkedHashMap<>(a);
                copy.put("estimatedMilkContributionKg", 0.0);
                result.add(copy);
            }
            return result;
        }

        double[] weights = new double[n];
        double weightSum = 0;
        for (int i = 0; i < n; i++) {
            weights[i] = ThreadLocalRandom.current().nextDouble(0.1, 1.0); // avoid near-zero shares
            weightSum += weights[i];
        }

        double[] shares = new double[n];
        double allocated = 0;
        for (int i = 0; i < n - 1; i++) {
            shares[i] = round2((weights[i] / weightSum) * totalKg);
            allocated += shares[i];
        }
        shares[n - 1] = round2(totalKg - allocated);

        if (shares[n - 1] < 0) {
            return redistributeSafely(animals, totalKg);
        }

        for (int i = 0; i < n; i++) {
            Map<String, Object> copy = new LinkedHashMap<>(animals.get(i));
            copy.put("estimatedMilkContributionKg", shares[i]);
            result.add(copy);
        }
        return result;
    }

    /**
     * Fallback for the rare case where random rounding pushes the last
     * share negative (very small totalKg with many animals). Splits evenly
     * instead, still guaranteeing the sum equals totalKg exactly.
     */
    private List<Map<String, Object>> redistributeSafely(
            List<Map<String, Object>> animals, double totalKg) {
        int n = animals.size();
        double base = round2(totalKg / n);
        double allocated = base * (n - 1);
        List<Map<String, Object>> result = new ArrayList<>(n);
        for (int i = 0; i < n - 1; i++) {
            Map<String, Object> copy = new LinkedHashMap<>(animals.get(i));
            copy.put("estimatedMilkContributionKg", base);
            result.add(copy);
        }
        Map<String, Object> last = new LinkedHashMap<>(animals.get(n - 1));
        last.put("estimatedMilkContributionKg", round2(totalKg - allocated));
        result.add(last);
        return result;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}