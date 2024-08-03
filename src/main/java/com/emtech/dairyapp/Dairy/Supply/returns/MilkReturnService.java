package com.emtech.dairyapp.Dairy.Supply.returns;

import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Dairy.Supply.MilkCollections;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
@AllArgsConstructor
public class MilkReturnService {
    @Autowired
    private final MilkCollectionRepo milkCollectionRepo;

    private final MilkReturnRepo milkReturnRepo;

    @Autowired
    private final FarmerRepo farmerRepo;

    @Autowired
    private final SmsServiceV2 smsServiceV2;


    public EntityResponse<?> returnDelivery(Long collectionId) {
        EntityResponse<?> response = new EntityResponse<>();

        try {
            Optional<MilkCollections> milkCollection = milkCollectionRepo.findById(collectionId);
            
            if (milkCollection.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Delivery not found");
                return response;
            }

            Optional<FarmerInfo> optional = farmerRepo.findByFarmerNo(milkCollection.get().getFarmerNo());

            if (optional.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Farmer not found");
                return response;
            }
            MilkCollections collections = milkCollection.get();
            MilkReturns milkReturns = getMilkReturns(milkCollection.get());
            FarmerInfo farmerInfo = optional.get();

            milkCollectionRepo.deleteById(collectionId);
            milkReturnRepo.save(milkReturns);

            // get year and month
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat formartYear = new SimpleDateFormat("yyyy");
            int month = Integer.parseInt(formatMonth.format(collections.getCollectionDate()));
            String year = formartYear.format(collections.getCollectionDate());

            Double monthTotal = milkCollectionRepo.getMonthyAccumulation(farmerInfo.getFarmer_no(), month, year);

            log.info("new month total for {} , farmer no {}, month {} , updated qty: {} .......", farmerInfo.getName(), farmerInfo.getFarmer_no(), month, monthTotal);

            String session = Objects.equals(collections.getSession(), "Session 1") ? "Morning" : (Objects.equals(collections.getSession(), "Session 2") ? "Afternoon" : "Evening");
            if (farmerInfo.getMobile_no() != null) {
                log.info("Sending sms ...");
                String message = "Dear " + farmerInfo.getName() + ", Farmer No. " + farmerInfo.getFarmer_no() + " we have returned " + collections.getQuantity() + "Kgs of milk. " +
                        session + " Session recorded on " + Formatter.formatDate(collections.getCollectionDate()) + ". Month Total: " + monthTotal + " Kgs. Helpline: 0726777884";
                String phoneno = Formatter.formatPhone(farmerInfo.getMobile_no().trim());
                smsServiceV2.SMSNotification(message, phoneno);
            }
            
            response.setMessage("Delivery Returned Successfully");
            response.setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(e.toString());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Unable to return delivery");
        }
        return response;
    }

    @NotNull
    private static MilkReturns getMilkReturns(MilkCollections colA) {
        MilkReturns milkReturns = new MilkReturns();

        milkReturns.setEvent(colA.getEvent());
        milkReturns.setCollectionDate(colA.getCollectionDate());
        milkReturns.setCollectorId(colA.getCollectorId());
        milkReturns.setDeductedWeight(colA.getDeductedWeight());
        milkReturns.setQuantity(colA.getQuantity());
        milkReturns.setOriginalQuantity(colA.getOriginalQuantity());
        milkReturns.setRouteFk(colA.getRouteFk());
        milkReturns.setFarmerNo(colA.getFarmerNo());
        milkReturns.setProductType(colA.getProductType());
        milkReturns.setRemarks(colA.getRemarks());
        milkReturns.setCollectionNumber(colA.getCollectionNumber());
        return milkReturns;
    }
}
