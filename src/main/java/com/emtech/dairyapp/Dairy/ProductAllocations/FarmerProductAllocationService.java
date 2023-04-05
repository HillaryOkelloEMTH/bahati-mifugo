package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Configurations.FarmerManagement.Farmer;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Utils.CONSTANTS;
import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Supply.MilkCollectionRepo;
import com.emtech.dairyapp.Response.EntityResponse;
import com.emtech.dairyapp.Stock.Product.Product;
import com.emtech.dairyapp.Stock.Product.ProductRepository;
import lombok.extern.slf4j.Slf4j;
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


    public EntityResponse addFarmerProductAllocations(FarmerProductAllocations allocation) {
        log.info("Adding new FarmerProductAllocations ...");
        EntityResponse response = new EntityResponse();
        Double amount = 0.0;
        Double salesPrice = 0.0;
        try {
            Optional<Farmer> f = farmerRepo.findById(allocation.getFarmerId());
            if (f.isPresent()) {
                Optional<Product> p = productRepository.findById(allocation.getProductId());
                if (p.isPresent()) {
                    Product product = p.get();
                    MilkCollectionRepo.Totals ut = milkCollectionRepo.getTotalUnPaidAmount(f.get().getFarmerNo());
                    Double unpaid= ut.getCollectionAmount();
                    salesPrice = product.getSalePrice();
                    amount = product.getSalePrice() * allocation.getQuantity();

                    if(amount>unpaid){

                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setMessage("Milk Collection income amount is too low");
                        return response;
                    }
                    allocation.setProductPrice(salesPrice);
                    allocation.setAmount(amount);
                    allocation.setAllocatioDate(new Date());
                    farmerProdAllocattionsRepo.save(allocation);
                    log.info("Saving Farmer Product Allocations ...");
                    response.setEntity(allocation);
                    response.setStatusCode(HttpStatus.CREATED.value());
                    response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                    return response;
                }else {
                    response.setEntity(allocation);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Product Not Found");
                    return response;
                }

            }else {

                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("Farmer not found!");
                return response;
            }

        } catch (Exception e) {
            log.error("Error: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            return response;
        }
    }

    public EntityResponse fetchFarmerProductAllocations() {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocations(CONSTANTS.NO);
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

    public EntityResponse fetchFarmerAllocations(Long farmerId) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsByFarmer(farmerId, CONSTANTS.NO);
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

    public EntityResponse fetchFarmerAllocationsBYDate(Long farmerId, String date) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getFAllocationsPerDate(farmerId, date);
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

    public EntityResponse fetchFarmerAllocationsByPaymentStatus(Long farmerId, Character paymentStatus) {
        log.info("Fetching FarmerProductAllocationss ...");
        EntityResponse response = new EntityResponse();
        try {
            List<Allocations> FarmerProductAllocationss = farmerProdAllocattionsRepo.getAllocationsByFarmerByPaymentStatus(farmerId, paymentStatus, CONSTANTS.NO);
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

    public EntityResponse getFarmerAccruals(Long farmerId, Character paymentStatus) {
        log.info("Fetching farmer accruals ...");
        EntityResponse response = new EntityResponse();
        try {
            FarmerProdAllocattionsRepo.FarmerAccruals accruals = farmerProdAllocattionsRepo.getFarmerAccruas(farmerId, paymentStatus);

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
            allocations.setAllocatioDate(new Date());
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
}
