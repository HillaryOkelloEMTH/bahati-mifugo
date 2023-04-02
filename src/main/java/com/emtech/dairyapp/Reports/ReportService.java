package com.emtech.dairyapp.Reports;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.FarmerCollections;

import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReportService {




    private final MilkCollectionRepo collectionRepo;


    public ReportService(MilkCollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
    }


    public Optional<CollectionsData> fetcCollectionsbyCode(String collectioncode) {
        try {
            return collectionRepo.getCollectionsbyCollectionCode(collectioncode);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<FarmerCollections> getFarmerCollections(Integer farmerNo) {
        try {

            return collectionRepo.getFarmerCollections(farmerNo);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public FarmerDetails getFarmerStatement(Integer farmerNo) {
        try {
            FarmerDetails f=null;
            Optional<FarmerDetails> farmerDetails=collectionRepo.getFarmerStatementDetails(farmerNo);
            if(farmerDetails.isPresent()){
                 f= farmerDetails.get();
            }
            return f;
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollecorColections(String date) {
        try {

            return collectionRepo.getCollectorsPerCollector(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<AnalyticsData> getCollectorLocations(String date) {
        try {

            return collectionRepo.getCollectorsPerLocation(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
    public List<ReportData> getDayCollections(String date) {
        try {

            return collectionRepo.getCollectorsPerDate(date);
        }catch (Exception exc){
            log.info(exc.getLocalizedMessage());
            return null;
        }
    }
}
