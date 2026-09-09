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
        if (farmerResponse == null || farmerResponse.get("entity") == null) {
            result.put("message", "Farmer not found on Mifugo360 (Maziwa Dairy)");
            result.put("statusCode", 404);
            return result;
        }

        Map<String, Object> farmerEntity = (Map<String, Object>) farmerResponse.get("entity");

        Object animalsObj = farmerEntity.get("animals");
        List<Map<String, Object>> animals = animalsObj instanceof List
                ? (List<Map<String, Object>>) animalsObj
                : Collections.emptyList();

        animals = rewriteMuzzleImageUrls(animals); // <-- new line

        Map<String, Object> farmerWithoutAnimals = new LinkedHashMap<>(farmerEntity);
        farmerWithoutAnimals.remove("animals");

        result.put("farmer", farmerWithoutAnimals);
        result.put("animals", animals);

        String nationalIdNo = (String) farmerEntity.get("nationalId");
        // 2. Bridge to local farmerNo, then pull milk collections summary
        Optional<Farmer> localFarmer = nationalIdNo != null
                ? farmerRepo.findByIdNumber(nationalIdNo)
                : Optional.empty();

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

            result.put("milkCollectionsSummary", summarizeCollections(deliveries));
        } else {
            result.put("localFarmerNo", null);
            result.put("milkCollectionsSummary", summarizeCollections(Collections.emptyList()));
            result.put("note", "No matching local farmer record found by nationalId; productivity unavailable.");
        }

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
    private List<Map<String, Object>> rewriteMuzzleImageUrls(List<Map<String, Object>> animals) {
        List<Map<String, Object>> rewritten = new ArrayList<>(animals.size());
        for (Map<String, Object> animal : animals) {
            Map<String, Object> copy = new LinkedHashMap<>(animal);
            Object muzzleImageObj = copy.get("muzzleImage");
            if (muzzleImageObj instanceof String) {
                String key = extractMuzzleKey((String) muzzleImageObj);
                if (key != null) {
                    animal.put("muzzleImage", maziwaBaseUrl +"/muzzles/" + key);
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

}