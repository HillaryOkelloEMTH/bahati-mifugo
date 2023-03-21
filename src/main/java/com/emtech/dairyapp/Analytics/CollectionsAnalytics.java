package com.emtech.dairyapp.Analytics;


import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CollectionsAnalytics {

    private final MilkCollectionRepo collectionRepo;

    public CollectionsAnalytics(MilkCollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
    }

    public EntityResponse getCollectionByDate(String date){

        EntityResponse response = new EntityResponse();
        try {

            List<AnalyticsData> todaysCollections= collectionRepo.getCollectorDataPerDate(date);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }
    public EntityResponse getCollectionByYear(Integer year){

        EntityResponse response = new EntityResponse();
        try {

            List<AnalyticsData> todaysCollections= collectionRepo.getCollectorDataPerYear(year);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(todaysCollections);
            response.setMessage(HttpStatus.OK.getReasonPhrase());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return response;
    }






}
