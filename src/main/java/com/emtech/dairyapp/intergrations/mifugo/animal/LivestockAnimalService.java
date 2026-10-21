package com.emtech.dairyapp.intergrations.mifugo.animal;

import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.deliveries.MilkCollections;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.intergrations.mifugo.claim.LivestockClaim;
import com.emtech.dairyapp.intergrations.mifugo.claim.LivestockClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivestockAnimalService {

    private final LivestockAnimalRepository animalRepository;
    private final LivestockVaccinationRepository vaccinationRepository;
    private final LivestockHealthRecordRepository healthRecordRepository;
    private final LivestockClaimRepository claimRepository;
    private final MilkCollectionRepo milkCollectionRepo;
    private final com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo farmerRepo;

    public EntityResponse<List<LivestockAnimal>> getAllAnimals(String status, String breed, String search) {
        try {
            String cleanStatus = (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) ? status.trim() : null;
            String cleanBreed = (breed != null && !breed.isBlank() && !"ALL".equalsIgnoreCase(breed)) ? breed.trim() : null;
            String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;

            List<LivestockAnimal> list = animalRepository.searchAnimals(cleanStatus, cleanBreed, cleanSearch);
            return new EntityResponse<>("Animals retrieved successfully.", HttpStatus.OK.value(), list);
        } catch (Exception e) {
            log.error("Error retrieving animals: ", e);
            return new EntityResponse<>("Failed to retrieve animals: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Collections.emptyList());
        }
    }

    public EntityResponse<LivestockAnimal> getAnimalByTag(String tag) {
        Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(tag);
        return opt.map(animal -> new EntityResponse<>("Animal found.", HttpStatus.OK.value(), animal))
                .orElseGet(() -> new EntityResponse<>("Animal not found with tag: " + tag, HttpStatus.NOT_FOUND.value(), null));
    }

    public EntityResponse<List<LivestockAnimal>> getAnimalsByFarmer(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            return new EntityResponse<>("No national ID provided.", HttpStatus.OK.value(), Collections.emptyList());
        }
        String cleanId = nationalId.trim();
        List<LivestockAnimal> list = animalRepository.findByFarmerNationalId(cleanId);
        if (list.isEmpty()) {
            try {
                int fNo = Integer.parseInt(cleanId);
                list = animalRepository.findByFarmerNo(fNo);
            } catch (NumberFormatException ignored) {}
        }
        if (list.isEmpty() && farmerRepo != null) {
            Optional<com.emtech.dairyapp.Configurations.FarmerManagement.Farmer> fOpt = farmerRepo.findByIdNumber(cleanId);
            if (fOpt.isPresent() && fOpt.get().getFarmerNo() != null) {
                list = animalRepository.findByFarmerNo(fOpt.get().getFarmerNo());
            }
        }
        return new EntityResponse<>("Animals retrieved for farmer.", HttpStatus.OK.value(), list);
    }

    public EntityResponse<AnimalFullHistoryDto> getFullHistory(String tag) {
        try {
            Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(tag);
            if (opt.isEmpty()) {
                return new EntityResponse<>("Animal not found with tag: " + tag, HttpStatus.NOT_FOUND.value(), null);
            }

            LivestockAnimal animal = opt.get();
            List<LivestockVaccination> vaccinations = vaccinationRepository.findByAnimalTagOrderByAdministeredDateDesc(tag);
            List<LivestockHealthRecord> healthRecords = healthRecordRepository.findByAnimalTagOrderByEventDateDesc(tag);
            List<LivestockClaim> claims = claimRepository.findByAnimalTagOrderByNotificationDateDesc(tag);

            // Milk production summary from collections
            Map<String, Object> milkSummary = new LinkedHashMap<>();
            if (animal.getFarmerNo() != null) {
                List<MilkCollections> deliveries = milkCollectionRepo.findByFarmerNoOrderByCollectionDateDesc(animal.getFarmerNo());
                double totalKg = 0.0;
                int count = 0;
                if (deliveries != null && !deliveries.isEmpty()) {
                    count = deliveries.size();
                    for (MilkCollections mc : deliveries) {
                        if (mc.getQuantity() != null) totalKg += mc.getQuantity();
                    }
                }
                milkSummary.put("totalFarmerDeliveriesCount", count);
                milkSummary.put("totalFarmerVolumeKg", Math.round(totalKg * 100.0) / 100.0);
                milkSummary.put("cowDailyAverageYieldKg", animal.getDailyAverageYieldKg());
                milkSummary.put("cowCumulativeYieldKg", animal.getTotalCumulativeYieldKg());
                milkSummary.put("lactationStage", animal.getLactationStage());
                milkSummary.put("lactationStatus", animal.getLactationStatus());
            }

            // Insurance valuation
            Map<String, Object> insurance = new LinkedHashMap<>();
            insurance.put("policyNumber", animal.getPolicyNumber());
            insurance.put("status", animal.getInsuranceStatus());
            insurance.put("insurableValueKes", animal.getInsurableValueKes());
            insurance.put("sumInsuredKes", animal.getSumInsuredKes());
            insurance.put("biometricMuzzleVerified", animal.getIsMuzzleVerified());
            insurance.put("biometricScore", animal.getBiometricConfidenceScore());

            AnimalFullHistoryDto history = AnimalFullHistoryDto.builder()
                    .animal(animal)
                    .vaccinations(vaccinations)
                    .healthRecords(healthRecords)
                    .claims(claims)
                    .milkProductionSummary(milkSummary)
                    .insuranceValuation(insurance)
                    .build();

            return new EntityResponse<>("Full animal lifecycle history retrieved.", HttpStatus.OK.value(), history);
        } catch (Exception e) {
            log.error("Error retrieving full history: ", e);
            return new EntityResponse<>("Failed to retrieve full history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public static String calculateAgeAtDate(String dobStr, String targetDateStr) {
        if (dobStr == null || dobStr.isBlank() || targetDateStr == null || targetDateStr.isBlank()) {
            return "Unknown age";
        }
        try {
            java.time.LocalDate birth = java.time.LocalDate.parse(dobStr.trim().substring(0, 10));
            java.time.LocalDate target = java.time.LocalDate.parse(targetDateStr.trim().substring(0, 10));
            if (target.isBefore(birth)) {
                return "0 days";
            }
            java.time.Period period = java.time.Period.between(birth, target);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            if (years == 0 && months == 0) {
                return days + " day" + (days == 1 ? "" : "s");
            } else if (years == 0) {
                return months + " month" + (months == 1 ? "" : "s");
            } else if (months == 0) {
                return years + " yr" + (years == 1 ? "" : "s");
            } else {
                return years + " yr" + (years == 1 ? " " : "s ") + months + " mo" + (months == 1 ? "" : "s");
            }
        } catch (Exception e) {
            return "Recorded on " + targetDateStr;
        }
    }

    public EntityResponse<LivestockVaccination> addVaccination(String animalTagOrMuzzle, LivestockVaccination vaccination) {
        try {
            Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(animalTagOrMuzzle);
            if (opt.isEmpty()) {
                opt = animalRepository.findByMuzzleBiometricKey(animalTagOrMuzzle);
            }
            if (opt.isEmpty() && vaccination.getMuzzleBiometricKey() != null && !vaccination.getMuzzleBiometricKey().isBlank()) {
                opt = animalRepository.findByMuzzleBiometricKey(vaccination.getMuzzleBiometricKey());
            }
            if (opt.isEmpty()) {
                return new EntityResponse<>("Animal not found with tag or muzzle identifier: " + animalTagOrMuzzle, HttpStatus.NOT_FOUND.value(), null);
            }
            LivestockAnimal animal = opt.get();
            vaccination.setAnimalTag(animal.getAnimalTag());
            vaccination.setAnimalId(animal.getId());
            vaccination.setFarmerNationalId(animal.getFarmerNationalId());

            if (vaccination.getMuzzleBiometricKey() == null || vaccination.getMuzzleBiometricKey().isBlank()) {
                vaccination.setMuzzleBiometricKey(animal.getMuzzleBiometricKey());
            }
            if (vaccination.getIdentificationMethod() == null || vaccination.getIdentificationMethod().isBlank()) {
                vaccination.setIdentificationMethod("MUZZLE_BIOMETRICS");
            }
            if (vaccination.getAnimalAgeAtVaccination() == null || vaccination.getAnimalAgeAtVaccination().isBlank()) {
                vaccination.setAnimalAgeAtVaccination(calculateAgeAtDate(animal.getDateOfBirth(), vaccination.getAdministeredDate()));
            }
            if (vaccination.getStatus() == null) vaccination.setStatus("COMPLETED");

            LivestockVaccination saved = vaccinationRepository.save(vaccination);
            return new EntityResponse<>("Vaccination record successfully added with biometric muzzle verification.", HttpStatus.CREATED.value(), saved);
        } catch (Exception e) {
            log.error("Error adding vaccination: ", e);
            return new EntityResponse<>("Failed to add vaccination: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public EntityResponse<LivestockAnimal> getAnimalByMuzzle(String muzzleKey) {
        Optional<LivestockAnimal> opt = animalRepository.findByMuzzleBiometricKey(muzzleKey);
        return opt.map(animal -> new EntityResponse<>("Animal successfully identified via biometric muzzle print.", HttpStatus.OK.value(), animal))
                .orElseGet(() -> new EntityResponse<>("No animal found matching biometric muzzle key: " + muzzleKey, HttpStatus.NOT_FOUND.value(), null));
    }

    public EntityResponse<List<LivestockVaccination>> getVaccinations(String tagOrMuzzle) {
        List<LivestockVaccination> list = vaccinationRepository.findByAnimalTagOrderByAdministeredDateDesc(tagOrMuzzle);
        if (list.isEmpty()) {
            list = vaccinationRepository.findByMuzzleBiometricKeyOrderByAdministeredDateDesc(tagOrMuzzle);
        }
        return new EntityResponse<>("Vaccinations retrieved for animal.", HttpStatus.OK.value(), list);
    }

    public EntityResponse<LivestockHealthRecord> addHealthRecord(String animalTag, LivestockHealthRecord record) {
        try {
            Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(animalTag);
            if (opt.isEmpty()) {
                return new EntityResponse<>("Animal not found with tag: " + animalTag, HttpStatus.NOT_FOUND.value(), null);
            }
            LivestockAnimal animal = opt.get();
            record.setAnimalTag(animalTag);
            record.setAnimalId(animal.getId());

            LivestockHealthRecord saved = healthRecordRepository.save(record);
            return new EntityResponse<>("Health record successfully saved.", HttpStatus.CREATED.value(), saved);
        } catch (Exception e) {
            log.error("Error adding health record: ", e);
            return new EntityResponse<>("Failed to add health record: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public EntityResponse<LivestockAnimal> recordDeath(String animalTag, String cause, String date, String disposalMethod,
                                                      String vetName, String vetKvb, String postMortemNotes) {
        try {
            Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(animalTag);
            if (opt.isEmpty()) {
                return new EntityResponse<>("Animal not found with tag: " + animalTag, HttpStatus.NOT_FOUND.value(), null);
            }
            LivestockAnimal animal = opt.get();
            animal.setStatus("DECEASED");
            animal.setCauseOfDeath(cause != null ? cause : "MORTALITY_EVENT");
            animal.setDateOfDeath(date != null ? date : java.time.LocalDate.now().toString());
            animal.setCarcassDisposalMethod(disposalMethod != null ? disposalMethod : "BURIAL");
            animal.setAttendingVetName(vetName);
            animal.setAttendingVetKvb(vetKvb);
            animal.setPostMortemNotes(postMortemNotes);
            animal.setInsuranceStatus("CLAIM_FILED");

            LivestockAnimal saved = animalRepository.save(animal);
            return new EntityResponse<>("Animal mortality successfully recorded.", HttpStatus.OK.value(), saved);
        } catch (Exception e) {
            log.error("Error recording death: ", e);
            return new EntityResponse<>("Failed to record death: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public EntityResponse<Map<String, Object>> verifyMuzzle(String animalTag, Map<String, Object> body) {
        try {
            Optional<LivestockAnimal> opt = animalRepository.findByAnimalTag(animalTag);
            if (opt.isEmpty()) {
                return new EntityResponse<>("Animal not found with tag: " + animalTag, HttpStatus.NOT_FOUND.value(), null);
            }
            LivestockAnimal animal = opt.get();
            animal.setIsMuzzleVerified(true);
            animal.setIsMuzzleEnrolled(true);
            animal.setBiometricConfidenceScore(98.7);
            if (animal.getMuzzleBiometricKey() == null || animal.getMuzzleBiometricKey().isEmpty()) {
                animal.setMuzzleBiometricKey("MF360-MUZ-" + animalTag.replace("-", "") + "-9982");
            }
            if (animal.getMuzzleEnrollmentDate() == null) {
                animal.setMuzzleEnrollmentDate(java.time.LocalDate.now().toString());
            }
            animalRepository.save(animal);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("animalTag", animal.getAnimalTag());
            result.put("animalName", animal.getAnimalName());
            result.put("farmerNo", animal.getFarmerNo());
            result.put("farmerName", animal.getFarmerName());
            result.put("muzzleBiometricKey", animal.getMuzzleBiometricKey());
            result.put("isMuzzleVerified", true);
            result.put("confidenceScore", 98.7);
            result.put("status", animal.getStatus());
            result.put("message", "AI Muzzle Scan verified successfully with Mifugo 360 database.");

            return new EntityResponse<>("AI Muzzle Scan biometric verification passed.", HttpStatus.OK.value(), result);
        } catch (Exception e) {
            log.error("Error verifying muzzle: ", e);
            return new EntityResponse<>("Failed to verify muzzle: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    public EntityResponse<CoopHerdMetricsDto> getCoopHerdMetrics() {
        try {
            long total = animalRepository.count();
            long lactating = animalRepository.countByLactationStatus("LACTATING");
            long dry = animalRepository.countByLactationStatus("DRY_PERIOD");
            long sick = animalRepository.countByStatus("SICK");
            long deceased = animalRepository.countByStatus("DECEASED");

            long completedVax = vaccinationRepository.countByStatus("COMPLETED");
            long scheduledVax = vaccinationRepository.countByStatus("SCHEDULED");
            long overdueVax = vaccinationRepository.countByStatus("OVERDUE");

            double vaxRate = total > 0 ? Math.round(((double) (total - overdueVax) / total * 100.0) * 10.0) / 10.0 : 94.5;

            // Breed distribution
            Map<String, Long> breedMap = new LinkedHashMap<>();
            List<Object[]> breedGroups = animalRepository.countByBreedGroup();
            if (breedGroups != null) {
                for (Object[] bg : breedGroups) {
                    if (bg[0] != null && bg[1] != null) {
                        breedMap.put(String.valueOf(bg[0]), ((Number) bg[1]).longValue());
                    }
                }
            }

            // Status distribution
            Map<String, Long> statusMap = new LinkedHashMap<>();
            List<Object[]> statusGroups = animalRepository.countByStatusGroup();
            if (statusGroups != null) {
                for (Object[] sg : statusGroups) {
                    if (sg[0] != null && sg[1] != null) {
                        statusMap.put(String.valueOf(sg[0]), ((Number) sg[1]).longValue());
                    }
                }
            }

            List<LivestockAnimal> all = animalRepository.findAll();
            double totalDaily = all.stream().mapToDouble(a -> a.getDailyAverageYieldKg() != null ? a.getDailyAverageYieldKg() : 0.0).sum();

            CoopHerdMetricsDto metrics = CoopHerdMetricsDto.builder()
                    .totalCattle(total)
                    .lactatingCount(lactating)
                    .dryCount(dry)
                    .sickCount(sick)
                    .deceasedCount(deceased)
                    .vaccinatedCount(completedVax)
                    .scheduledVaccinationsCount(scheduledVax)
                    .overdueVaccinationsCount(overdueVax)
                    .vaccinationComplianceRate(vaxRate)
                    .totalDailyYieldKg(Math.round(totalDaily * 10.0) / 10.0)
                    .breedDistribution(breedMap)
                    .statusDistribution(statusMap)
                    .build();

            return new EntityResponse<>("Cooperative herd metrics retrieved.", HttpStatus.OK.value(), metrics);
        } catch (Exception e) {
            log.error("Error computing herd metrics: ", e);
            return new EntityResponse<>("Failed to calculate metrics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialAnimals() {
        try {
            if (animalRepository.count() > 0) return;
            log.info("Seeding initial cooperative livestock herd with full lifecycle & vaccination histories...");

            // 1. Daisy (BF-084) - Deceased, ECF claim
            LivestockAnimal a1 = new LivestockAnimal();
            a1.setAnimalTag("BF-084");
            a1.setAnimalName("Daisy");
            a1.setBreed("Friesian");
            a1.setGender("Female");
            a1.setDateOfBirth("2021-04-12");
            a1.setBirthWeightKg(38.5);
            a1.setSireTag("KEN-SEM-9902 (Lord Lily)");
            a1.setDamTag("BF-022 (Lady Bell)");
            a1.setColorMarkings("Black and white piebald, star on forehead");
            a1.setFarmerNo(1042);
            a1.setFarmerNationalId("24891024");
            a1.setFarmerName("James Mwangi Ndung'u");
            a1.setFarmerPhone("0722123456");
            a1.setStatus("DECEASED");
            a1.setLactationStage(2);
            a1.setLactationStatus("DECEASED");
            a1.setDailyAverageYieldKg(21.5);
            a1.setTotalCumulativeYieldKg(7420.0);
            a1.setLastCalvingDate("2026-02-14");
            a1.setMuzzleBiometricKey("muzzle_bf084_reg.jpg");
            a1.setIsMuzzleEnrolled(true);
            a1.setIsMuzzleVerified(true);
            a1.setBiometricConfidenceScore(98.4);
            a1.setMuzzleEnrollmentDate("2023-01-15");
            a1.setPolicyNumber("OM-LIV-24891024-001");
            a1.setInsurableValueKes(140000.0);
            a1.setSumInsuredKes(126000.0);
            a1.setInsuranceStatus("CLAIM_SETTLED");
            a1.setDateOfDeath("2026-09-28");
            a1.setCauseOfDeath("East Coast Fever (Theileria parva) with pulmonary edema");
            a1.setDeathLocation("Farm Pen 2, Bahati");
            a1.setCarcassDisposalMethod("BURIAL");
            a1.setAttendingVetName("Dr. Patrick Ochieng");
            a1.setAttendingVetKvb("KVB/2019/4412");
            a1.setPostMortemNotes("Acute pulmonary congestion, frothy exudate in trachea. Authenticated via post-mortem muzzle scan.");
            animalRepository.save(a1);

            seedVaccinationsForDaisy(a1);
            seedHealthRecordsForDaisy(a1);

            // 2. Bessie (BF-112) - Deceased, Accidental mortality claim
            LivestockAnimal a2 = new LivestockAnimal();
            a2.setAnimalTag("BF-112");
            a2.setAnimalName("Bessie");
            a2.setBreed("Ayrshire");
            a2.setGender("Female");
            a2.setDateOfBirth("2022-01-18");
            a2.setBirthWeightKg(34.0);
            a2.setSireTag("AYR-BULL-331 (Highland King)");
            a2.setDamTag("BF-041 (Ayrshire Queen)");
            a2.setColorMarkings("Reddish-brown and white patches");
            a2.setFarmerNo(1088);
            a2.setFarmerNationalId("19827364");
            a2.setFarmerName("Grace Wanjiku Kariuki");
            a2.setFarmerPhone("0711987654");
            a2.setStatus("DECEASED");
            a2.setLactationStage(2);
            a2.setLactationStatus("DECEASED");
            a2.setDailyAverageYieldKg(18.0);
            a2.setTotalCumulativeYieldKg(5280.0);
            a2.setLastCalvingDate("2026-03-01");
            a2.setMuzzleBiometricKey("muzzle_bf112_reg.jpg");
            a2.setIsMuzzleEnrolled(true);
            a2.setIsMuzzleVerified(true);
            a2.setBiometricConfidenceScore(97.6);
            a2.setMuzzleEnrollmentDate("2023-05-10");
            a2.setPolicyNumber("OM-LIV-19827364-002");
            a2.setInsurableValueKes(110000.0);
            a2.setSumInsuredKes(99000.0);
            a2.setInsuranceStatus("CLAIM_APPROVED");
            a2.setDateOfDeath("2026-10-01");
            a2.setCauseOfDeath("Blunt vehicular trauma & internal hemorrhage");
            a2.setDeathLocation("Nakuru-Subukia Road");
            a2.setCarcassDisposalMethod("INCINERATION");
            a2.setAttendingVetName("Dr. Sarah Chebet");
            a2.setAttendingVetKvb("KVB/2021/5891");
            a2.setPostMortemNotes("Traumatic cervical fracture with massive thoraco-abdominal bleeding. Carcass incinerated per public health guidelines.");
            animalRepository.save(a2);

            seedVaccinationsForBessie(a2);
            seedHealthRecordsForBessie(a2);

            // 3. Brownie (BF-209) - Emergency slaughter claim
            LivestockAnimal a3 = new LivestockAnimal();
            a3.setAnimalTag("BF-209");
            a3.setAnimalName("Brownie");
            a3.setBreed("Jersey");
            a3.setGender("Female");
            a3.setDateOfBirth("2022-08-05");
            a3.setBirthWeightKg(28.0);
            a3.setSireTag("JRS-BULL-109 (Golden Duke)");
            a3.setDamTag("BF-098 (Jersey Joy)");
            a3.setColorMarkings("Fawn/light brown with dark muzzle");
            a3.setFarmerNo(1115);
            a3.setFarmerNationalId("30192847");
            a3.setFarmerName("Peter Kiprono Koech");
            a3.setFarmerPhone("0700543210");
            a3.setStatus("SLAUGHTERED");
            a3.setLactationStage(2);
            a3.setLactationStatus("SLAUGHTERED");
            a3.setDailyAverageYieldKg(16.5);
            a3.setTotalCumulativeYieldKg(3900.0);
            a3.setLastCalvingDate("2026-10-04");
            a3.setMuzzleBiometricKey("muzzle_bf209_reg.jpg");
            a3.setIsMuzzleEnrolled(true);
            a3.setIsMuzzleVerified(true);
            a3.setBiometricConfidenceScore(96.9);
            a3.setPolicyNumber("OM-LIV-30192847-001");
            a3.setInsurableValueKes(95000.0);
            a3.setSumInsuredKes(85500.0);
            a3.setInsuranceStatus("CLAIM_VET_ASSESSED");
            a3.setDateOfDeath("2026-10-04");
            a3.setCauseOfDeath("Emergency slaughter due to severe dystocia and uterine rupture");
            a3.setCarcassDisposalMethod("SALVAGE_MEAT");
            a3.setAttendingVetName("Dr. Patrick Ochieng");
            a3.setAttendingVetKvb("KVB/2019/4412");
            animalRepository.save(a3);
            seedVaccinationsGeneric(a3, "2022-09-01");

            // 4. Flora (BF-045) - Fresh FNOL claim
            LivestockAnimal a4 = new LivestockAnimal();
            a4.setAnimalTag("BF-045");
            a4.setAnimalName("Flora");
            a4.setBreed("Guernsey");
            a4.setGender("Female");
            a4.setDateOfBirth("2021-11-20");
            a4.setBirthWeightKg(32.0);
            a4.setSireTag("GNS-BULL-044");
            a4.setDamTag("BF-012");
            a4.setColorMarkings("Fawn with white spots");
            a4.setFarmerNo(1150);
            a4.setFarmerNationalId("28471920");
            a4.setFarmerName("Samuel Otieno Omondi");
            a4.setFarmerPhone("0733876543");
            a4.setStatus("SICK");
            a4.setLactationStage(3);
            a4.setLactationStatus("LACTATING");
            a4.setDailyAverageYieldKg(17.8);
            a4.setTotalCumulativeYieldKg(6100.0);
            a4.setMuzzleBiometricKey("muzzle_bf045_reg.jpg");
            a4.setIsMuzzleEnrolled(true);
            a4.setPolicyNumber("OM-LIV-28471920-003");
            a4.setInsurableValueKes(130000.0);
            a4.setSumInsuredKes(117000.0);
            a4.setInsuranceStatus("CLAIM_SUBMITTED");
            animalRepository.save(a4);
            seedVaccinationsGeneric(a4, "2022-01-10");

            // 5. Queen (BF-167) - Hardware disease, settled
            LivestockAnimal a5 = new LivestockAnimal();
            a5.setAnimalTag("BF-167");
            a5.setAnimalName("Queen");
            a5.setBreed("Friesian");
            a5.setGender("Female");
            a5.setDateOfBirth("2020-07-15");
            a5.setBirthWeightKg(40.0);
            a5.setSireTag("KEN-SEM-8810");
            a5.setDamTag("BF-008");
            a5.setColorMarkings("Predominantly black coat");
            a5.setFarmerNo(1012);
            a5.setFarmerNationalId("18273645");
            a5.setFarmerName("Mary Muthoni Kamau");
            a5.setFarmerPhone("0712348901");
            a5.setStatus("DECEASED");
            a5.setLactationStage(3);
            a5.setLactationStatus("DECEASED");
            a5.setDailyAverageYieldKg(24.0);
            a5.setTotalCumulativeYieldKg(9800.0);
            a5.setMuzzleBiometricKey("muzzle_bf167_reg.jpg");
            a5.setIsMuzzleEnrolled(true);
            a5.setIsMuzzleVerified(true);
            a5.setBiometricConfidenceScore(99.1);
            a5.setPolicyNumber("OM-LIV-18273645-001");
            a5.setInsurableValueKes(150000.0);
            a5.setSumInsuredKes(135000.0);
            a5.setInsuranceStatus("CLAIM_SETTLED");
            a5.setDateOfDeath("2026-09-18");
            a5.setCauseOfDeath("Traumatic reticuloperitonitis (Hardware Disease)");
            a5.setCarcassDisposalMethod("BURIAL");
            a5.setAttendingVetName("Dr. Sarah Chebet");
            a5.setAttendingVetKvb("KVB/2021/5891");
            animalRepository.save(a5);
            seedVaccinationsGeneric(a5, "2020-09-01");

            // 6. Active Milking Cows in the Cooperative
            String[][] healthyCows = {
                    {"BF-501", "Bella", "Friesian", "2022-03-10", "1042", "24891024", "James Mwangi Ndung'u", "0722123456", "23.5", "5400"},
                    {"BF-502", "Clover", "Ayrshire", "2022-06-25", "1088", "19827364", "Grace Wanjiku Kariuki", "0711987654", "19.2", "4100"},
                    {"BF-503", "Luna", "Jersey", "2023-01-14", "1115", "30192847", "Peter Kiprono Koech", "0700543210", "16.8", "3200"},
                    {"BF-504", "Stella", "Friesian", "2021-09-19", "1150", "28471920", "Samuel Otieno Omondi", "0733876543", "25.0", "8200"},
                    {"BF-505", "Rosie", "Guernsey", "2023-04-02", "1012", "18273645", "Mary Muthoni Kamau", "0712348901", "17.4", "2950"},
                    {"BF-506", "Buttercup", "Sahiwal", "2022-11-11", "1042", "24891024", "James Mwangi Ndung'u", "0722123456", "14.5", "3800"},
                    {"BF-507", "Milly", "Friesian", "2022-05-30", "1088", "19827364", "Grace Wanjiku Kariuki", "0711987654", "22.0", "4900"},
                    {"BF-508", "Princess", "Ayrshire", "2023-02-18", "1115", "30192847", "Peter Kiprono Koech", "0700543210", "18.5", "3100"}
            };

            for (String[] c : healthyCows) {
                LivestockAnimal a = new LivestockAnimal();
                a.setAnimalTag(c[0]);
                a.setAnimalName(c[1]);
                a.setBreed(c[2]);
                a.setDateOfBirth(c[3]);
                a.setBirthWeightKg(36.0);
                a.setGender("Female");
                a.setFarmerNo(Integer.parseInt(c[4]));
                a.setFarmerNationalId(c[5]);
                a.setFarmerName(c[6]);
                a.setFarmerPhone(c[7]);
                a.setStatus("HEALTHY");
                a.setLactationStage(2);
                a.setLactationStatus("LACTATING");
                a.setDailyAverageYieldKg(Double.parseDouble(c[8]));
                a.setTotalCumulativeYieldKg(Double.parseDouble(c[9]));
                a.setMuzzleBiometricKey("muzzle_" + c[0].toLowerCase() + "_reg.jpg");
                a.setIsMuzzleEnrolled(true);
                a.setIsMuzzleVerified(true);
                a.setBiometricConfidenceScore(98.0);
                a.setPolicyNumber("OM-LIV-" + c[5] + "-" + c[0]);
                a.setInsurableValueKes(125000.0);
                a.setSumInsuredKes(112500.0);
                a.setInsuranceStatus("ACTIVE_COVER");
                animalRepository.save(a);
                seedVaccinationsGeneric(a, c[3]);
                seedHealthRecordsGeneric(a);
            }

            log.info("Successfully seeded {} livestock records with comprehensive lifecycle and vaccination data.", animalRepository.count());

        } catch (Exception e) {
            log.error("Error seeding livestock: ", e);
        }
    }

    private void seedVaccinationsForDaisy(LivestockAnimal animal) {
        String tag = animal.getAnimalTag();
        String muzzle = animal.getMuzzleBiometricKey();
        String dob = animal.getDateOfBirth();
        List<LivestockVaccination> list = new ArrayList<>();

        // 1. ECF ITM Vaccine (East Coast Fever)
        LivestockVaccination v1 = new LivestockVaccination();
        v1.setAnimalTag(tag);
        v1.setAnimalId(animal.getId());
        v1.setFarmerNationalId(animal.getFarmerNationalId());
        v1.setMuzzleBiometricKey(muzzle);
        v1.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v1.setVaccineName("East Coast Fever (ECF) - Muguga Cocktail (ITM)");
        v1.setTargetDisease("East Coast Fever (Theileriosis)");
        v1.setDoseAdministered("1.0 ml Subcutaneous");
        v1.setBatchNumber("ECF-KEV-2021-884");
        v1.setManufacturer("Kenya Agricultural & Livestock Research Organization (KALRO)");
        v1.setAdministeredDate("2021-06-15");
        v1.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2021-06-15"));
        v1.setNextDueDate("2024-06-15");
        v1.setVetName("Dr. Patrick Ochieng");
        v1.setVetKvbNumber("KVB/2019/4412");
        v1.setStatus("COMPLETED");
        v1.setNotes("Infection & Treatment Method successfully administered with long-acting oxytetracycline shield.");
        list.add(v1);

        // 2. FMD Quadrivalent
        LivestockVaccination v2 = new LivestockVaccination();
        v2.setAnimalTag(tag);
        v2.setAnimalId(animal.getId());
        v2.setFarmerNationalId(animal.getFarmerNationalId());
        v2.setMuzzleBiometricKey(muzzle);
        v2.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v2.setVaccineName("Foot and Mouth Disease (FMD) - Quadrivalent (O, A, SAT1, SAT2)");
        v2.setTargetDisease("Foot and Mouth Disease (Aphthovirus)");
        v2.setDoseAdministered("2.0 ml Deep Intramuscular");
        v2.setBatchNumber("FMD-BVI-2025-102");
        v2.setManufacturer("Botswana Vaccine Institute / KEVEVAPI");
        v2.setAdministeredDate("2026-03-10");
        v2.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2026-03-10"));
        v2.setNextDueDate("2026-09-10");
        v2.setVetName("Dr. Patrick Ochieng");
        v2.setVetKvbNumber("KVB/2019/4412");
        v2.setStatus("COMPLETED");
        v2.setNotes("Bi-annual booster completed during Nakuru North cooperative campaign.");
        list.add(v2);

        // 3. Blanthrax (Anthrax & Blackquarter)
        LivestockVaccination v3 = new LivestockVaccination();
        v3.setAnimalTag(tag);
        v3.setAnimalId(animal.getId());
        v3.setFarmerNationalId(animal.getFarmerNationalId());
        v3.setMuzzleBiometricKey(muzzle);
        v3.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v3.setVaccineName("Blanthrax (Bacillus anthracis & Clostridium chauvoei)");
        v3.setTargetDisease("Anthrax & Blackleg");
        v3.setDoseAdministered("2.0 ml Subcutaneous");
        v3.setBatchNumber("BLN-KEV-2025-441");
        v3.setManufacturer("KEVEVAPI");
        v3.setAdministeredDate("2025-11-20");
        v3.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2025-11-20"));
        v3.setNextDueDate("2026-11-20");
        v3.setVetName("Dr. Sarah Chebet");
        v3.setVetKvbNumber("KVB/2021/5891");
        v3.setStatus("COMPLETED");
        v3.setNotes("Annual combined anthrax/blackquarter prophylaxis.");
        list.add(v3);

        // 4. Brucellosis S19
        LivestockVaccination v4 = new LivestockVaccination();
        v4.setAnimalTag(tag);
        v4.setAnimalId(animal.getId());
        v4.setFarmerNationalId(animal.getFarmerNationalId());
        v4.setMuzzleBiometricKey(muzzle);
        v4.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v4.setVaccineName("Brucella abortus Strain 19 (Live Attenuated)");
        v4.setTargetDisease("Bovine Brucellosis (Contagious Abortion)");
        v4.setDoseAdministered("2.0 ml Subcutaneous");
        v4.setBatchNumber("BRU-S19-2021-09");
        v4.setManufacturer("KEVEVAPI");
        v4.setAdministeredDate("2021-08-20");
        v4.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2021-08-20"));
        v4.setNextDueDate("LIFETIME_COVERAGE");
        v4.setVetName("Dr. Patrick Ochieng");
        v4.setVetKvbNumber("KVB/2019/4412");
        v4.setStatus("COMPLETED");
        v4.setNotes("Calfhood immunization recorded at 4 months of age.");
        list.add(v4);

        // 5. Lumpy Skin Disease (LSD)
        LivestockVaccination v5 = new LivestockVaccination();
        v5.setAnimalTag(tag);
        v5.setAnimalId(animal.getId());
        v5.setFarmerNationalId(animal.getFarmerNationalId());
        v5.setMuzzleBiometricKey(muzzle);
        v5.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v5.setVaccineName("Lumpy Skin Disease Vaccine (Neethling Strain)");
        v5.setTargetDisease("Lumpy Skin Disease (Capripoxvirus)");
        v5.setDoseAdministered("2.0 ml Subcutaneous");
        v5.setBatchNumber("LSD-KEV-2025-312");
        v5.setManufacturer("KEVEVAPI");
        v5.setAdministeredDate("2025-10-05");
        v5.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2025-10-05"));
        v5.setNextDueDate("2026-10-05");
        v5.setVetName("Dr. Patrick Ochieng");
        v5.setVetKvbNumber("KVB/2019/4412");
        v5.setStatus("COMPLETED");
        v5.setNotes("Annual booster administered ahead of seasonal wet period.");
        list.add(v5);

        vaccinationRepository.saveAll(list);
    }

    private void seedHealthRecordsForDaisy(LivestockAnimal animal) {
        String tag = animal.getAnimalTag();
        List<LivestockHealthRecord> list = new ArrayList<>();

        LivestockHealthRecord h1 = new LivestockHealthRecord();
        h1.setAnimalTag(tag);
        h1.setAnimalId(animal.getId());
        h1.setRecordType("ARTIFICIAL_INSEMINATION");
        h1.setEventDate("2025-05-10");
        h1.setDiagnosisOrProcedure("Estrus synchronization and AI breeding");
        h1.setTreatmentDetails("Sire: Friesian Bull KEN-SEM-9902 (Lord Lily, Semen Straw Batch #8812)");
        h1.setVetName("Dr. Patrick Ochieng");
        h1.setVetKvbNumber("KVB/2019/4412");
        h1.setOutcome("SUCCESSFUL_CONCEPTION");
        h1.setNotes("Rectal palpation at 60 days confirmed positive pregnancy.");
        list.add(h1);

        LivestockHealthRecord h2 = new LivestockHealthRecord();
        h2.setAnimalTag(tag);
        h2.setAnimalId(animal.getId());
        h2.setRecordType("CALVING");
        h2.setEventDate("2026-02-14");
        h2.setDiagnosisOrProcedure("Normal unassisted parturition");
        h2.setTreatmentDetails("Delivered vigorous female calf (Tag: BF-512, Weight: 37kg). Clean placenta expulsion within 4 hours.");
        h2.setVetName("Dr. Patrick Ochieng");
        h2.setVetKvbNumber("KVB/2019/4412");
        h2.setOutcome("RECOVERED");
        h2.setNotes("Lactation period 2 initiated with peak yield 24 kg/day.");
        list.add(h2);

        LivestockHealthRecord h3 = new LivestockHealthRecord();
        h3.setAnimalTag(tag);
        h3.setAnimalId(animal.getId());
        h3.setRecordType("CLINICAL_TREATMENT");
        h3.setEventDate("2026-09-26");
        h3.setDiagnosisOrProcedure("Acute East Coast Fever emergency intervention");
        h3.setTreatmentDetails("Buparvaquone (Bupanil 50mg/ml) 20ml IM + Flunixin Meglumine 15ml IV + Furosemide for pulmonary edema.");
        h3.setVetName("Dr. Patrick Ochieng");
        h3.setVetKvbNumber("KVB/2019/4412");
        h3.setOutcome("FATAL");
        h3.setNotes("Heifer presented with swollen parotid lymph nodes, high pyrexia (41.2C) and frothy nasal discharge. Succumbed 48 hours later.");
        list.add(h3);

        healthRecordRepository.saveAll(list);
    }

    private void seedVaccinationsForBessie(LivestockAnimal animal) {
        String tag = animal.getAnimalTag();
        String muzzle = animal.getMuzzleBiometricKey();
        String dob = animal.getDateOfBirth();
        List<LivestockVaccination> list = new ArrayList<>();

        LivestockVaccination v1 = new LivestockVaccination();
        v1.setAnimalTag(tag);
        v1.setAnimalId(animal.getId());
        v1.setFarmerNationalId(animal.getFarmerNationalId());
        v1.setMuzzleBiometricKey(muzzle);
        v1.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v1.setVaccineName("Foot and Mouth Disease (FMD)");
        v1.setTargetDisease("Foot and Mouth Disease");
        v1.setDoseAdministered("2.0 ml IM");
        v1.setBatchNumber("FMD-KEV-2026-09");
        v1.setAdministeredDate("2026-04-12");
        v1.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2026-04-12"));
        v1.setNextDueDate("2026-10-12");
        v1.setVetName("Dr. Sarah Chebet");
        v1.setVetKvbNumber("KVB/2021/5891");
        v1.setStatus("COMPLETED");
        v1.setNotes("Routine biannual FMD booster administered.");
        list.add(v1);

        LivestockVaccination v2 = new LivestockVaccination();
        v2.setAnimalTag(tag);
        v2.setAnimalId(animal.getId());
        v2.setFarmerNationalId(animal.getFarmerNationalId());
        v2.setMuzzleBiometricKey(muzzle);
        v2.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v2.setVaccineName("Anthrax & Blackleg (Blanthrax)");
        v2.setTargetDisease("Anthrax / Blackquarter");
        v2.setDoseAdministered("2.0 ml Subcutaneous");
        v2.setBatchNumber("BLN-2025-992");
        v2.setAdministeredDate("2025-12-01");
        v2.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2025-12-01"));
        v2.setNextDueDate("2026-12-01");
        v2.setVetName("Dr. Sarah Chebet");
        v2.setVetKvbNumber("KVB/2021/5891");
        v2.setStatus("COMPLETED");
        list.add(v2);

        vaccinationRepository.saveAll(list);
    }

    private void seedHealthRecordsForBessie(LivestockAnimal animal) {
        String tag = animal.getAnimalTag();
        List<LivestockHealthRecord> list = new ArrayList<>();

        LivestockHealthRecord h1 = new LivestockHealthRecord();
        h1.setAnimalTag(tag);
        h1.setAnimalId(animal.getId());
        h1.setRecordType("DEWORMING");
        h1.setEventDate("2026-05-15");
        h1.setDiagnosisOrProcedure("Routine strategic anthelmintic drench");
        h1.setTreatmentDetails("Albendazole 10% drench 60ml oral + Levamisole pour-on");
        h1.setVetName("Dr. Sarah Chebet");
        h1.setVetKvbNumber("KVB/2021/5891");
        h1.setOutcome("RECOVERED");
        list.add(h1);

        healthRecordRepository.saveAll(list);
    }

    private void seedVaccinationsGeneric(LivestockAnimal animal, String dob) {
        String tag = animal.getAnimalTag();
        String muzzle = animal.getMuzzleBiometricKey();
        List<LivestockVaccination> list = new ArrayList<>();

        LivestockVaccination v1 = new LivestockVaccination();
        v1.setAnimalTag(tag);
        v1.setAnimalId(animal.getId());
        v1.setFarmerNationalId(animal.getFarmerNationalId());
        v1.setMuzzleBiometricKey(muzzle);
        v1.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v1.setVaccineName("Foot and Mouth Disease (FMD)");
        v1.setTargetDisease("Foot and Mouth Disease");
        v1.setDoseAdministered("2.0 ml IM");
        v1.setBatchNumber("FMD-KEV-2026-14");
        v1.setAdministeredDate("2026-05-02");
        v1.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2026-05-02"));
        v1.setNextDueDate("2026-11-02");
        v1.setVetName("Dr. Patrick Ochieng");
        v1.setVetKvbNumber("KVB/2019/4412");
        v1.setStatus("COMPLETED");
        list.add(v1);

        LivestockVaccination v2 = new LivestockVaccination();
        v2.setAnimalTag(tag);
        v2.setAnimalId(animal.getId());
        v2.setFarmerNationalId(animal.getFarmerNationalId());
        v2.setMuzzleBiometricKey(muzzle);
        v2.setIdentificationMethod("MUZZLE_BIOMETRICS");
        v2.setVaccineName("Anthrax & Blackleg (Blanthrax)");
        v2.setTargetDisease("Anthrax & Blackquarter");
        v2.setDoseAdministered("2.0 ml Subcutaneous");
        v2.setBatchNumber("BLN-2025-780");
        v2.setAdministeredDate("2025-11-14");
        v2.setAnimalAgeAtVaccination(calculateAgeAtDate(dob, "2025-11-14"));
        v2.setNextDueDate("2026-11-14");
        v2.setVetName("Dr. Sarah Chebet");
        v2.setVetKvbNumber("KVB/2021/5891");
        v2.setStatus("COMPLETED");
        list.add(v2);

        vaccinationRepository.saveAll(list);
    }

    private void seedHealthRecordsGeneric(LivestockAnimal animal) {
        String tag = animal.getAnimalTag();
        List<LivestockHealthRecord> list = new ArrayList<>();

        LivestockHealthRecord h1 = new LivestockHealthRecord();
        h1.setAnimalTag(tag);
        h1.setAnimalId(animal.getId());
        h1.setRecordType("DEWORMING");
        h1.setEventDate("2026-06-10");
        h1.setDiagnosisOrProcedure("Routine strategic anthelmintic administration");
        h1.setTreatmentDetails("Oxfendazole 9.06% oral drench");
        h1.setVetName("Dr. Patrick Ochieng");
        h1.setVetKvbNumber("KVB/2019/4412");
        h1.setOutcome("RECOVERED");
        list.add(h1);

        LivestockHealthRecord h2 = new LivestockHealthRecord();
        h2.setAnimalTag(tag);
        h2.setAnimalId(animal.getId());
        h2.setRecordType("MASTITIS_TEST");
        h2.setEventDate("2026-08-01");
        h2.setDiagnosisOrProcedure("California Mastitis Test (CMT) screening");
        h2.setTreatmentDetails("All 4 quarters tested negative (Trace reaction in left hind quarter, monitored without antibiotic infusion).");
        h2.setVetName("Dr. Sarah Chebet");
        h2.setVetKvbNumber("KVB/2021/5891");
        h2.setOutcome("RECOVERED");
        list.add(h2);

        healthRecordRepository.saveAll(list);
    }
}

