package com.emtech.dairyapp.Reports;

import com.emtech.dairyapp.Analytics.AnalyticsData;
import com.emtech.dairyapp.Dairy.Interface.CollectionsData;
import com.emtech.dairyapp.Dairy.Interface.Statement;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Response.EntityResponse;
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
    public List<Statement> getFarmerStatement(Long farmerId) {
        try {

            return collectionRepo.getFarmerstatement(farmerId);
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
}
