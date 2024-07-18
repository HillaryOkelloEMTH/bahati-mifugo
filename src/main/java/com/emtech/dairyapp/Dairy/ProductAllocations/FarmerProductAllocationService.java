package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Auth.Utilities.RequestStatus;
import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerInfo;
import com.emtech.dairyapp.Configurations.Interfaces.PickUpLocation;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocations;
import com.emtech.dairyapp.Configurations.PickUpLocations.PickUpLocationsRepo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Configurations.Utils.Formatter;
import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.ProductAllocations.dto.ProductRequestDto;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Notifications.SMS.smsv2.SmsServiceV2;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.Stock.MccAllocations.MccAllocation;
import com.emtech.dairyapp.Stock.MccAllocations.MccAllocationRepo;
import com.emtech.dairyapp.Stock.Product.Product;
import com.emtech.dairyapp.Stock.Product.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FarmerProductAllocationService {
    @Autowired
    private MilkCollectionRepo milkCollectionRepo;


    @Autowired
    private FarmerProdAllocattionsRepo farmerProdAllocattionsRepo;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FarmerRepo farmerRepo;

    @Autowired
    private PickUpLocationsRepo pickUpLocationsRepo;

    @Autowired
    private MccAllocationRepo mccAllocationRepo;

    @Autowired
    private SmsServiceV2 smsServiceV2;

    public EntityResponse<?> addFarmerProductAllocations(ProductRequestDto productRequest) {
        log.info("Adding new farmer product request ...");
        EntityResponse<?> response = new EntityResponse<>();

        try {
            Optional<FarmerInfo> f = farmerRepo.findByFarmerNo(productRequest.getFarmerNo());
            FarmerProdAllocattionsRepo.FarmerAllocationData farmerIncome = farmerProdAllocattionsRepo.getMonthlyAmount(productRequest.getFarmerNo());
            Optional<MccAllocation> optionalMccAllocation = mccAllocationRepo.findByProductIdAndLocationId(productRequest.getProductId(), productRequest.getLocationId());

            log.info("checking farmer existence for farmer no {} ........ ", productRequest.getFarmerNo());
            if (f.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Farmer not found!");
                return response;
            }

            if (optionalMccAllocation.isEmpty()) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("No products found for specified mcc");
                return response;
            }
            MccAllocation mccAllocation = optionalMccAllocation.get();

            if (mccAllocation.getStock() < productRequest.getQuantity()) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Requested quantity is greater than available stock");
                return response;
            }

            MilkCollectionRepo.Totals ut = milkCollectionRepo.getTotalUnPaidAmount(f.get().getFarmer_no());
            if (ut.getCollectionAmount() == null) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("No revenue found for this month");
                return response;
            }

            if (productRequest.getAmount() > ut.getCollectionAmount()) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Product price is higher than monthly income");
                return response;
            }

            FarmerProductAllocations request = getFarmerProductAllocations(productRequest);

            farmerProdAllocattionsRepo.save(request);

            log.info("Saving Farmer Product Allocations ...");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage(HttpStatus.CREATED.getReasonPhrase());

            if (f.get().getMobile_no() != null) {
                log.info("sending acknowledgement sms to farmer {} , farmer no {} ", f.get().getUsername(), productRequest.getFarmerNo());
                String formattedPhone = Formatter.formatPhone(f.get().getMobile_no());
                String message = "Dear "+f.get().getName()+" farmer no "+productRequest.getFarmerNo()+
                        ". We have received your request for "+productRequest.getQuantity()+
                        " units of "+productRequest.getProductName()+" on " + Formatter.formatDate(new Date());

                smsServiceV2.SMSNotification(message, formattedPhone);
            }

        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
        return  response;
    }


    public EntityResponse<?> fetchFarmerProductAllocations() {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocations(CONSTANTS.NO);
            if (FarmerProductAllocationss.size() > 0) {

                log.info("FarmerProductAllocations Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocationss Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse<?> fetchMccFarmerProductAllocations(Long locationId) {
        log.info("Fetching Mcc Farmer Product Allocations ........");
        EntityResponse<List<Allocations>> response = new EntityResponse<>();
        try {
            Optional<PickUpLocations> pickUpLocation = pickUpLocationsRepo.findById(locationId);
            List<Allocations> mccAllocations = farmerProdAllocattionsRepo.getMccAllocations(locationId);

            if (pickUpLocation.isEmpty()) {
                log.info("Pick Up Location Not Found for id " + locationId);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Pick Up Location Not Found for id " + locationId);
                return response;
            }


            if (!mccAllocations.isEmpty()) {
                log.info("FarmerProductAllocations Found " + "(" + mccAllocations.size() + ")");
                response.setEntity(mccAllocations);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocations Not Found ");
                response.setEntity(mccAllocations);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Product Allocations Not Found for "+pickUpLocation.get().getName());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }
    public EntityResponse<?> updateStatus(Long id,String status) {
        log.info("verify  product allocations ...");
        EntityResponse<FarmerProductAllocations> response = new EntityResponse<>();
        try {
            Optional<FarmerProductAllocations> farmerAllocation = farmerProdAllocattionsRepo.findById(id);
            if (farmerAllocation.isPresent()) {

                FarmerProductAllocations f= farmerAllocation.get();
                if(status.equalsIgnoreCase("Approved")){
                    f.setStatus(RequestStatus.APPROVED);
                    f.setApprovalDate(new Date());
                }else if (status.equalsIgnoreCase("Rejected")){
                    f.setStatus(RequestStatus.REJECTED);
                }

                Optional<MccAllocation> allocationOptional = mccAllocationRepo.findByProductIdAndLocationId(f.getProductId(), f.getLocationId());

                if (allocationOptional.isEmpty()) {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("No products found for specified mcc");
                    return response;
                }

                log.info("checking farmer existence for farmer no {} ........ ", f.getFarmerNo());
                Optional<FarmerInfo> farmerInfo = farmerRepo.findByFarmerNo(f.getFarmerNo());
                if (farmerInfo.isEmpty()) {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Farmer not found!");
                    return response;
                }
                MccAllocation mccAllocation = allocationOptional.get();


                log.info("checking if requested quantity is available in mcc stock");
                if (mccAllocation.getStock() < f.getQuantity()) {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("requested quantity is lesser than stock");
                    return response;
                }

                FarmerInfo farmer = farmerInfo.get();

                log.info("Updating the stock level after allocation .......");
                mccAllocation.setStock(mccAllocation.getStock() - f.getQuantity());
                mccAllocationRepo.save(mccAllocation);

                farmerProdAllocattionsRepo.save(f);
                response.setEntity(farmerAllocation.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Successfully allocated "+f.getQuantity()+" units of "+f.getProductName()+" to "+f.getFarmerName());

                if (farmer.getMobile_no() != null) {
                    log.info("sending approval sms to farmer {} , farmer no {} , phone no {} ....", farmer.getName(), f.getFarmerNo(), farmer.getMobile_no());
                    String formattedPhone = Formatter.formatPhone(farmer.getMobile_no());
                    String message = "Dear "+farmer.getName()+" farmer no "+f.getFarmerNo()+
                            ", your request for "+f.getQuantity()+
                            " units of "+f.getProductName()+" has been approved on " + Formatter.formatDate(new Date());

                smsServiceV2.SMSNotification(message, formattedPhone);
                }

            } else {
                log.info("Farmer Product Allocations Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerProductAllocationsPerType(String type) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsPerType(type);
            if (FarmerProductAllocationss.size() > 0) {
                log.info("FarmerProductAllocationss Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocationss Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerAllocations(Integer farmerNo) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsByFarmer(farmerNo, CONSTANTS.NO);
            if (FarmerProductAllocationss.size() > 0) {
                log.info("FarmerProductAllocationss Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocationss Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchAllocationsByDate(String date) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsByDate(date);
            if (FarmerProductAllocationss.size() > 0) {
                log.info("FarmerProductAllocationss Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocationss Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerAllocationsBYDate(Integer farmerNO, String date) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getFAllocationsPerDate(farmerNO, date);
            if (FarmerProductAllocationss.size() > 0) {
                log.info("FarmerProductAllocationss Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocationss Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerAllocationsByPaymentStatus(Integer farmerNO, Character paymentStatus) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsByFarmerByPaymentStatus(farmerNO, paymentStatus, CONSTANTS.NO);
            if (FarmerProductAllocationss.size() > 0) {
                log.info("FarmerProductAllocationss Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
            } else {
                log.info("FarmerProductAllocations Not Found " + "(" + FarmerProductAllocationss.size() + ")");
                response.setEntity(FarmerProductAllocationss);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
            }
            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse getFarmerAccruals(Integer farmerNO, Character paymentStatus) {
        log.info("Fetching farmer accruals ...");
        EntityResponse response = new EntityResponse();
        try {
            FarmerProdAllocattionsRepo.FarmerAccruals accruals = farmerProdAllocattionsRepo.getFarmerAccruas(farmerNO, paymentStatus);

            response.setEntity(accruals);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.FOUND.getReasonPhrase());

            return response;
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse updateFarmerProductAllocations(FarmerProductAllocations allocations) {
        EntityResponse response = new EntityResponse();
        try {
            allocations.setAllocationDate(new Date());
            FarmerProductAllocations al = farmerProdAllocattionsRepo.save(allocations);
            response.setEntity(al);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            return response;


        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse revoke(Long id) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<FarmerProductAllocations> allocations = farmerProdAllocattionsRepo.findById(id);
            if (allocations.isPresent()) {
                allocations.get().setRevokeStatus(CONSTANTS.YES);
                farmerProdAllocattionsRepo.save(allocations.get());
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("FarmerProductAllocations deleted Successfully");
                return response;

            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("FarmerProductAllocations with id " + id + "Not Found");
                return response;

            }
        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    @NotNull
    private static FarmerProductAllocations getFarmerProductAllocations(ProductRequestDto productRequest) {
        FarmerProductAllocations request = new FarmerProductAllocations();
        request.setProductPrice(productRequest.getPrice());
        request.setProductId(productRequest.getProductId());
        request.setAmount(productRequest.getAmount());
        request.setProductName(productRequest.getProductName());
        request.setRequestedOn(new Date());
        request.setFarmerNo(productRequest.getFarmerNo());
        request.setComments(productRequest.getComments());
        request.setType(productRequest.getType());
        request.setFarmerName(productRequest.getFarmerName());
        request.setLocationId(productRequest.getLocationId());
        request.setQuantity(productRequest.getQuantity());
        return request;
    }
}
