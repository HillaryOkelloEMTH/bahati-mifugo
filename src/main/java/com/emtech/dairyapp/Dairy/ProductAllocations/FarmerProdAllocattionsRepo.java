package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.Interface.Allocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerProdAllocattionsRepo extends JpaRepository<FarmerProductAllocations,Long> {



    List<FarmerProductAllocations> findByFarmerId(Long id);

    @Query(value = "SELECT f.id,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.id = a.farmer_id join  product p on p.id =a.product_id and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocations(Character revoke_status);
    @Query(value = "SELECT f.id,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.id = a.farmer_id join  product p on p.id =a.product_id WHERE a.farmer_id = :farmer_id and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmer(Long farmer_id,Character revoke_status);
    @Query(value = "SELECT f.id,f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.id = a.farmer_id join  product p on p.id =a.product_id WHERE a.farmer_id = :farmer_id and  a.payment_status=:payment_status and a.revoke_status =:revoke_status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmerByPaymentStatus(Long farmer_id,Character payment_status,Character revoke_status);
    @Query(value = "SELECT f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus  from farmer_product_allocations a join farmer f  on f.id = a.farmer_id join  product p on p.id =a.product_id WHERE DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getAllocationsByDate(String date);
    @Query(value = "SELECT f.username,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus  from farmer_product_allocations a join farmer f  on f.id = a.farmer_id join  product p on p.id =a.product_id WHERE a.farmer_id = :farmer_id  and DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getFAllocationsPerDate(Long farmer_id,String date);
}
