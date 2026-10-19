package com.emtech.dairyapp.Analytics;


import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollectionRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CollectionsAnalytics {

    private final MilkCollectionRepo collectionRepo;
    private final com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo farmerRepo;
    private final com.emtech.dairyapp.intergrations.mifugo.animal.LivestockAnimalRepository animalRepository;
    private final com.emtech.dairyapp.intergrations.mifugo.animal.LivestockVaccinationRepository vaccinationRepository;
    private final com.emtech.dairyapp.intergrations.mifugo.claim.LivestockClaimRepository claimRepository;
    private final com.emtech.dairyapp.intergrations.mifugo.wallet.FarmerWalletRepository walletRepository;

    public CollectionsAnalytics(
            MilkCollectionRepo collectionRepo,
            com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo farmerRepo,
            com.emtech.dairyapp.intergrations.mifugo.animal.LivestockAnimalRepository animalRepository,
            com.emtech.dairyapp.intergrations.mifugo.animal.LivestockVaccinationRepository vaccinationRepository,
            com.emtech.dairyapp.intergrations.mifugo.claim.LivestockClaimRepository claimRepository,
            com.emtech.dairyapp.intergrations.mifugo.wallet.FarmerWalletRepository walletRepository) {
        this.collectionRepo = collectionRepo;
        this.farmerRepo = farmerRepo;
        this.animalRepository = animalRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.claimRepository = claimRepository;
        this.walletRepository = walletRepository;
    }

    public EntityResponse<?> getBahatiDailySummary(Integer month, Integer year) {
        EntityResponse<List<MilkCollectionRepo.DailySummary>> response = new EntityResponse<>();


        try {
            List<MilkCollectionRepo.DailySummary> data = collectionRepo.getBahatiDailySummary(month, year);

            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("retrieved records for "+data.size()+" days");
            response.setEntity(data);
        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An error occurred");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(new LinkedList<>());
        }
        return response;
    }

    public EntityResponse<?> getMccDailySummary(Long locationId, Integer month, Integer year) {
        EntityResponse<List<MilkCollectionRepo.DailySummary>> response = new EntityResponse<>();

        try {
            List<MilkCollectionRepo.DailySummary> data = collectionRepo.getMccDailySummary(locationId, month, year);

            response.setMessage("Retrieved "+data.size()+" records");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(data);
        } catch (Exception e) {
            log.error(e.toString());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("An error occurred");
        }
        return response;
    }

    public EntityResponse<?> getCollectionByDate(String date) {

        EntityResponse<List<AnalyticsData>> response = new EntityResponse<>();
        try {

            List<AnalyticsData> collections = collectionRepo.getCollectorDataPerDate(date);

            LinkedStringInteger data = new LinkedStringInteger();


            LinkedList<String> names = new LinkedList<>();
            LinkedList<Double> amount = new LinkedList<>();
            LinkedList<Double> quantity = new LinkedList<>();

            for (AnalyticsData c : collections) {

                names.add(c.getCollector());
                amount.add(c.getAmount());
                quantity.add(c.getAmount());

            }
            data.setQuantity(quantity);
            data.setNames(names);
            data.setAmount(amount);


            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(collections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse<?> getCollectionByYear(Integer year) {

        EntityResponse<List<AnalyticsData>> response = new EntityResponse<>();
        try {
            List<AnalyticsData> collections = collectionRepo.getCollectorDataPerYear(year);
//            LinkedStringInteger data = new LinkedStringInteger();


//                LinkedList<String> names = new LinkedList<>();
//                LinkedList<Double> amount = new LinkedList<>();
//                LinkedList<Double> quantity = new LinkedList<>();
//
//                for (AnalyticsData c : collections) {
//
//                    names.add(c.getMonth());
//                    amount.add(c.getAmount());
//                    quantity.add(c.getAmount());
//
//
//                }
//                data.setQuantiy(quantity);
//                data.setNames(names);
//                data.setAmount(amount);


            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(collections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionPerLocation() {

        EntityResponse response = new EntityResponse();
        try {
            List<AnalyticsData> collections = collectionRepo.getQuantityPerLocation();
//            LinkedStringInteger data = new LinkedStringInteger();
//
//
//            LinkedList<String> names = new LinkedList<>();
//            LinkedList<Double> quantity = new LinkedList<>();
//
//            for (AnalyticsData a : collections) {
//
//                names.add(a.getLocation());
//                quantity.add(a.getQuantity());
//
//
//
//            }
//            data.setQuantiy(quantity);
//            data.setNames(names);
//

            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(collections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }


    public EntityResponse getCollectionByMontheAndYear(Integer year, Integer month, Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {
            Optional<AnalyticsData> collections = collectionRepo.getCollectorRecord(year, month, collectorId);
            if (collections.isPresent()) {
                AnalyticsData a = collections.get();
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(a);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections.get());
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByMonthAndYearandSesson(Integer year, Integer month, Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {
            List<AnalyticsData> collections = collectionRepo.getCollectorDataPerSerssion(year, month, collectorId);
            if (collections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());

            }


        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }

    public EntityResponse getCollectionByMonth(Integer year, Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {
            List<AnalyticsData> collections = collectionRepo.getQuantityPerMonth(year, collectorId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(collections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setEntity(new LinkedList<>());
        }
        return response;
    }

    public EntityResponse getCollectorCollectionsPerMonth(Integer year, Integer month) {

        EntityResponse response = new EntityResponse();
        try {
            List<AnalyticsData> collections = collectionRepo.getCollectorCollections(year, month);
            if (collections.size() > 0) {
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.OK.getReasonPhrase());
            } else {

                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(collections);
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getCollectionCount(Integer year, Long collectorId) {

        EntityResponse response = new EntityResponse();
        try {
            List<AnalyticsData> collections = collectionRepo.getCollectionCountPerMonth(year, collectorId);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(collections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setEntity(new LinkedList<>());
        }
        return response;
    }

    public EntityResponse<CooperativeDashboardDto> getCooperativeDashboard() {
        EntityResponse<CooperativeDashboardDto> response = new EntityResponse<>();
        try {
            // 1. Milk Collection statistics
            var todayList = collectionRepo.getTodaysCollections();
            double todayKg = 0.0;
            long todayCount = 0;
            if (todayList != null && !todayList.isEmpty()) {
                var first = todayList.get(0);
                todayKg = first.getQuantity() != null ? first.getQuantity() : 0.0;
                todayCount = first.getCount() != null ? first.getCount() : 0;
            }

            var allDeliveries = collectionRepo.findAll();
            // Robust check: in case of native query or date/timezone boundary differences, verify against allDeliveries
            if ((todayKg == 0.0 || todayCount == 0) && allDeliveries != null && !allDeliveries.isEmpty()) {
                java.time.LocalDate today = java.time.LocalDate.now();
                var todaysDeliveries = allDeliveries.stream()
                        .filter(c -> c.getCollectionDate() != null)
                        .filter(c -> {
                            java.time.LocalDate cDate = c.getCollectionDate().toInstant()
                                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return cDate.equals(today);
                        })
                        .toList();
                if (!todaysDeliveries.isEmpty()) {
                    todayKg = todaysDeliveries.stream()
                            .mapToDouble(c -> c.getQuantity() != null ? c.getQuantity() : 0.0).sum();
                    todayCount = todaysDeliveries.size();
                }
            }
            double totalQuantityKg = allDeliveries.stream()
                    .mapToDouble(c -> c.getQuantity() != null ? c.getQuantity() : 0.0).sum();
            double totalAmountKes = allDeliveries.stream()
                    .mapToDouble(c -> c.getAmount() != null ? c.getAmount() : (c.getQuantity() != null ? c.getQuantity() * 50.0 : 0.0)).sum();

            long activeSuppliers = allDeliveries.stream()
                    .map(c -> c.getFarmerNo()).filter(java.util.Objects::nonNull).distinct().count();
            long totalFarmers = farmerRepo.count();

            // 2. Animal Herd Statistics
            long totalCattle = animalRepository.count();
            long lactatingCount = animalRepository.countByLactationStatus("LACTATING");
            long dryCount = animalRepository.countByLactationStatus("DRY_PERIOD");
            long sickCount = animalRepository.countByStatus("SICK");

            long completedVax = vaccinationRepository.countByStatus("COMPLETED");
            long overdueVax = vaccinationRepository.countByStatus("OVERDUE");
            double vaxCompliance = totalCattle > 0 ? Math.round(((double) (totalCattle - overdueVax) / totalCattle * 100.0) * 10.0) / 10.0 : 96.0;

            var allAnimals = animalRepository.findAll();
            double dailyYield = allAnimals.stream()
                    .mapToDouble(a -> a.getDailyAverageYieldKg() != null ? a.getDailyAverageYieldKg() : 0.0).sum();

            // 3. Cooperative Insurance Claims Standing
            long totalClaims = claimRepository.count();
            long settledClaims = claimRepository.countByClaimStatus("SETTLED");
            long approvedClaims = claimRepository.countByClaimStatus("APPROVED");
            long pendingClaims = claimRepository.countByClaimStatus("SUBMITTED") + claimRepository.countByClaimStatus("UNDER_INVESTIGATION") + claimRepository.countByClaimStatus("VET_ASSESSED");
            Double totalPayouts = walletRepository.sumTotalDisbursedClaims();

            // 4. Daily milk volume trend (current month)
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            int currentYear = java.time.LocalDate.now().getYear();
            var rawDaily = collectionRepo.getBahatiDailySummary(currentMonth, currentYear);
            java.util.List<java.util.Map<String, Object>> dailyTrend = new java.util.ArrayList<>();
            if (rawDaily != null) {
                for (var ds : rawDaily) {
                    java.util.Map<String, Object> point = new java.util.LinkedHashMap<>();
                    point.put("day", ds.getDay());
                    point.put("quantity", ds.getQty());
                    dailyTrend.add(point);
                }
            }

            // 5. Breed Distribution
            java.util.Map<String, Long> breedMap = new java.util.LinkedHashMap<>();
            java.util.List<Object[]> breedGroups = animalRepository.countByBreedGroup();
            if (breedGroups != null) {
                for (Object[] bg : breedGroups) {
                    if (bg[0] != null && bg[1] != null) {
                        breedMap.put(String.valueOf(bg[0]), ((Number) bg[1]).longValue());
                    }
                }
            }

            // 6. Herd Status Distribution
            java.util.Map<String, Long> statusMap = new java.util.LinkedHashMap<>();
            java.util.List<Object[]> statusGroups = animalRepository.countByStatusGroup();
            if (statusGroups != null) {
                for (Object[] sg : statusGroups) {
                    if (sg[0] != null && sg[1] != null) {
                        statusMap.put(String.valueOf(sg[0]), ((Number) sg[1]).longValue());
                    }
                }
            }

            // 7. Location / MCC intake
            java.util.List<java.util.Map<String, Object>> locationList = new java.util.ArrayList<>();
            var locData = collectionRepo.getQuantityPerLocation();
            if (locData != null) {
                for (AnalyticsData ad : locData) {
                    java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("location", ad.getLocation() != null ? ad.getLocation() : "Bahati Central");
                    row.put("quantity", ad.getQuantity() != null ? ad.getQuantity() : 0.0);
                    locationList.add(row);
                }
            }

            // 7b. Recent Milk Deliveries
            java.util.List<java.util.Map<String, Object>> recentDeliveriesList = new java.util.ArrayList<>();
            if (allDeliveries != null && !allDeliveries.isEmpty()) {
                var sortedDeliveries = allDeliveries.stream()
                        .filter(c -> c.getCollectionDate() != null)
                        .sorted((a, b) -> b.getCollectionDate().compareTo(a.getCollectionDate()))
                        .limit(8)
                        .toList();
                for (var del : sortedDeliveries) {
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id", del.getId());
                    map.put("farmerNo", del.getFarmerNo());
                    map.put("collectionNumber", del.getCollectionNumber());
                    map.put("quantity", del.getQuantity());
                    map.put("amount", del.getAmount());
                    map.put("session", del.getSession());
                    map.put("productType", del.getProductType());
                    map.put("collectionDate", del.getCollectionDate());
                    map.put("canNo", del.getCanNo());
                    recentDeliveriesList.add(map);
                }
            }

            // 8. Recent lodged claims (preview for cooperative)
            java.util.List<java.util.Map<String, Object>> recentClaimsList = new java.util.ArrayList<>();
            var claims = claimRepository.findAllByOrderByCreatedAtDesc();
            if (claims != null) {
                for (int i = 0; i < Math.min(claims.size(), 5); i++) {
                    var c = claims.get(i);
                    java.util.Map<String, Object> claimMap = new java.util.LinkedHashMap<>();
                    claimMap.put("claimReference", c.getClaimReference());
                    claimMap.put("farmerName", c.getFarmerName());
                    claimMap.put("animalTag", c.getAnimalTag());
                    claimMap.put("claimType", c.getClaimType());
                    claimMap.put("claimStatus", c.getClaimStatus());
                    claimMap.put("netPayableAmount", c.getNetPayableAmount());
                    claimMap.put("incidentDate", c.getIncidentDate());
                    recentClaimsList.add(claimMap);
                }
            }

            CooperativeDashboardDto dto = CooperativeDashboardDto.builder()
                    .todayQuantityKg(Math.round(todayKg * 10.0) / 10.0)
                    .todayCollectionsCount(todayCount)
                    .totalMilkQuantityKg(Math.round(totalQuantityKg * 10.0) / 10.0)
                    .totalMilkAmountKes(Math.round(totalAmountKes * 10.0) / 10.0)
                    .activeSupplyingFarmersCount(activeSuppliers)
                    .totalRegisteredFarmersCount(totalFarmers)
                    .totalCattleHerd(totalCattle)
                    .lactatingCowsCount(lactatingCount)
                    .dryCowsCount(dryCount)
                    .sickCowsCount(sickCount)
                    .vaccinationComplianceRate(vaxCompliance)
                    .totalDailyEstimatedYieldKg(Math.round(dailyYield * 10.0) / 10.0)
                    .totalClaimsLodged(totalClaims)
                    .claimsApprovedOrSettled(settledClaims + approvedClaims)
                    .claimsPendingVerification(pendingClaims)
                    .totalClaimPayoutsReceivedKes(totalPayouts != null ? totalPayouts : 0.0)
                    .dailyMilkTrend(dailyTrend)
                    .breedDistribution(breedMap)
                    .herdStatusDistribution(statusMap)
                    .locationIntake(locationList)
                    .recentDeliveries(recentDeliveriesList)
                    .recentLodgedClaims(recentClaimsList)
                    .build();

            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Cooperative dashboard data retrieved successfully.");
            response.setEntity(dto);

        } catch (Exception e) {
            log.error("Error generating cooperative dashboard: ", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Failed to retrieve cooperative dashboard: " + e.getMessage());
        }
        return response;
    }


}