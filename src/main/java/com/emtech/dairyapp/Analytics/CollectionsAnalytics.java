package com.emtech.dairyapp.Analytics;


import com.emtech.dairyapp.Configurations.Interfaces.FarmersPerWard;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
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

    public CollectionsAnalytics(MilkCollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
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
            data.setQuantiy(quantity);
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
            if (collections.size() > 0) {

//                LinkedStringInteger data = new LinkedStringInteger();
//                LinkedList<String> months = new LinkedList<>();
//                LinkedList<Double> quantity = new LinkedList<>();
//                LinkedList<Double> amount = new LinkedList<>();
//
//                for (AnalyticsData d : collections) {
//                    months.add(d.getMonth());
//                    quantity.add(d.getQuantity());
//                    amount.add(d.getAmount());
//                }
//                data.setNames(months);
//                data.setAmount(amount);
//                data.setQuantiy(quantity);


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


}