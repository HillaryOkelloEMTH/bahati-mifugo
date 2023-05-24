package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Interface.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerProdAllocattionsRepo extends JpaRepository<FarmerProductAllocations,Long> {



    List<FarmerProductAllocations> findByFarmerNo(Integer farmerNo);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,a.status,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.status =:status",nativeQuery = true)
    List<Allocations> getAllocations(Character status);
    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.type =:type",nativeQuery = true)
    List<Allocations> getAllocationsPerType(String type);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.type = 'Service' and a.service_status = :status",nativeQuery = true)
    List<Services> getAllServicesByServiceStatus(String status);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.type = 'Service'",nativeQuery = true)
    List<Services> getAllServiceApplications();
    @Query(value = "SELECT a.id,f.farmer_no, f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and a.status =:status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmer(Integer farmer_no,Character status);
    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and  a.payment_status=:payment_status and a.status =:status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmerByPaymentStatus(Integer farmer_no,Character payment_status,Character status);
    @Query(value = "SELECT f.username,f.farmer_no,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus  from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getAllocationsByDate(String date);
    @Query(value = "SELECT a.username,f.farmer_no,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no  and DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getFAllocationsPerDate(Integer farmer_no,String date);
    @Query(value = "SELECT SUM(fa.amount) as accruedamount,f.farmer_no,f.username  from farmer_product_allocations fa join farmer f on f.farmer_no =fa.farmer_no where DATE(fa.allocatio_date) BETWEEN :from and :to and f.farmer_no =:farmer_no and  fa.payment_status =:payment_status",nativeQuery = true)
    FarmerAllocationDatail getFAllocationsSummary(Integer farmer_no,String from ,String to,Character payment_status);


    interface FarmerAllocationDatail{
        Double getAccruedamount();
        String getUsername();
        Integer getFarmer_no();

    }

    @Query(value = "SELECT SUM(fa.amount) as accruedamount,SUM(fa.quantity) as quantity  from farmer_product_allocations fa join farmer f on f.farmer_no =fa.farmer_no where f.farmer_no =:farmer_no and  fa.payment_status =:payment_status",nativeQuery = true)
    FarmerAccruals getFarmerAccruas(Integer farmer_no,Character payment_status);


    interface FarmerAccruals{
        Double getAccruedamount();
        Double getQuantity();

    }

    @Query(nativeQuery = true,value = "SELECT p.name as product, CONVERT(a.farmer_no,CHAR)as farmer_no  ,COALESCE(ROUND(a.amount ,2),0.0) as amount,IFNULL (a.quantity,'-') as quantity ,a.status,IFNULL(DATE_FORMAT(a.heat_start_date,'%Y-%m-%d'),'-')as heat_start_date ,IFNULL(a.no_of_cows,'-') as noOfCows,a.`type`,DATE_FORMAT(a.allocatio_date ,'%Y-%m-%d') as allocationDate  from farmer_product_allocations a join product p on p.id =a.product_id  WHERE MONTHNAME(a.allocatio_date)=:month  and a.farmer_no =:farmer_no")
    List<FarmerProducts> getFarmerProduct(Integer farmer_no,String month);

    @Query(nativeQuery = true,value = "SELECT COALESCE(ROUND(SUM(a.amount),2),0.0) as amount  from farmer_product_allocations a WHERE a.status='Y' and a.farmer_no =:farmer_no and a.payment_status =:payment_status and MONTHNAME(a.allocatio_date)=:month")
    Double getFPAmount(Integer farmer_no,Character payment_status,String month);


}
