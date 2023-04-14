package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerProdAllocattionsRepo extends JpaRepository<FarmerProductAllocations,Long> {



    List<FarmerProductAllocations> findByFarmerNo(Integer farmerNo);

    @Query(value = "SELECT f.id,f.farmer_no,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocations(Character revoke_status);
    @Query(value = "SELECT f.id,f.farmer_no,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmer(Integer farmer_no,Character revoke_status);
    @Query(value = "SELECT f.id,f.farmer_no,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and  a.payment_status=:payment_status and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmerByPaymentStatus(Integer farmer_no,Character payment_status,Character revoke_status);
    @Query(value = "SELECT f.username,f.farmer_no,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus  from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getAllocationsByDate(String date);
    @Query(value = "SELECT f.username,f.farmer_no,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus  from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no  and DATE(a.allocatio_date)=:date",nativeQuery = true)
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
}
