package com.emtech.dairyapp.Dairy.ProductAllocations;

import com.emtech.dairyapp.Dairy.Interface.AllocationDataInterface;
import com.emtech.dairyapp.Dairy.Interface.Allocations;
import com.emtech.dairyapp.Dairy.Interface.Services;
import org.hibernate.bytecode.enhance.spi.interceptor.AbstractLazyLoadInterceptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerProdAllocattionsRepo extends JpaRepository<FarmerProductAllocations,Long> {



    List<FarmerProductAllocations> findByFarmerNoOrderByRequestedOnDesc(Integer farmerNo);

    @Query(value = "select count(*) from farmer_product_allocations al where al.status='APPROVED' and al.location_id = :locationId  and monthname(al.approval_date)= :month and year(al.approval_date)= :year", nativeQuery = true)
    Integer getMccAllocationsCount(Long locationId, String month, String year);

    @Query(value = "SELECT coalesce(SUM(fa.amount), 0.0) as accruedamount,SUM(fa.quantity) as quantity  from farmer_product_allocations fa join farmer f on f.farmer_no =fa.farmer_no where f.farmer_no =:farmer_no and fa.status='APPROVED' and month(fa.approval_date)=month(curdate()) and year(fa.approval_date)=year(curdate())",nativeQuery = true)
    FarmerAllocationData getMonthlyAmount(Integer farmer_no);

    @Query(value = "select * from farmer_product_allocations where type='Good'", nativeQuery = true)
    List<Allocations> getProductAllocations();

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.approval_date as approvalDate,a.type,a.status,p.name as product,a.requested_on as requestedOn,r.route, pul.name as location, a.amount as amount,a.quantity as quantity,DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id " +
            "join route r on f.route_fk=r.id join pick_up_locations pul on r.location_id=pul.id order by a.requested_on desc",nativeQuery = true)
    List<Allocations> getAllocations(Character status, Pageable pageable);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.approval_date as approvalDate,a.type,a.status,p.name as product,a.requested_on as requestedOn,r.route, pul.name as location, a.amount as amount,a.quantity as quantity,DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id " +
                      "join route r on f.route_fk=r.id join pick_up_locations pul on r.location_id=pul.id where date(a.requested_on) between :from and :to order by a.requested_on desc", nativeQuery = true)
    List<Allocations> getAllocationsByDateRange(String from, String to);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,a.status,a.requested_on as requestedOn, p.name as product,a.amount as amount,a.quantity as quantity, a.approval_date as approvalDate, DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id where a.location_id= :locationId and month(a.requested_on)= :month and year(a.requested_on)= :year",nativeQuery = true)
    List<Allocations> getMccAllocations(Long locationId, Integer month, String year);

    @Query(value = "SELECT a.id,f.farmer_no,f.username, a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,a.status,a.requested_on as requestedOn, p.name as product,r.route, a.amount as amount,a.quantity as quantity, a.approval_date as approvalDate, DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id join route r on r.id=f.route_fk where f.route_fk = :routeId and month(a.requested_on)= :month and year(a.requested_on)= :year", nativeQuery = true)
    List<Allocations> getRouteAllocations(Long routeId, Integer month, String year);

    @Query(value = "select fpa.farmer_no, fpa.farmer_name as farmer, date(fpa.requested_on) as requested_on, date(fpa.approval_date) as approval_date , fpa.product_name as product, fpa.quantity, fpa.amount, fpa.status, pul.name as mcc from farmer_product_allocations fpa join pick_up_locations pul on fpa.location_id = pul.id where month(requested_on)= :month and year(requested_on)= :year order by pul.id asc, fpa.product_id", nativeQuery = true)
    List<AllocationDataInterface> getAllocationsByPeriod(Integer month, String year);
    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id and a.type =:type",nativeQuery = true)
    List<Allocations> getAllocationsPerType(String type);

    @Query(value = "SELECT a.id,f.farmer_no, f.mobile_no as mobileNo, r.route as route, f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join  product p on p.id =a.product_id and a.type = 'Service' and a.service_status = :status",nativeQuery = true)
    List<Services> getAllServicesByServiceStatus(String status);

    @Query(value = "SELECT a.id,f.farmer_no, f.mobile_no as mobileNo, r.route as route,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join product p on p.id =a.product_id and a.type = 'Service' and a.service_status <> 'Pending'",nativeQuery = true)
    List<Services> fetchAllServicesHistoryApplications();

    @Query(value = "SELECT a.id,f.farmer_no, f.mobile_no as mobileNo, r.route as route,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join  product p on p.id =a.product_id and a.type = 'Service' and a.farmer_no= :memberNo and a.service_status <> 'Pending'",nativeQuery = true)
    List<Services> fetchAllServicesHistoryApplicationsByFarmerNo(Long memberNo);

    @Query(value = "SELECT a.id,f.farmer_no,f.username, f.mobile_no as mobileNo, r.route as route,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join product p on p.id =a.product_id and a.type = 'Service' and a.farmer_no= :memberNo and DATE(a.allocatio_date) between :fromDate and :toDate and a.service_status <> 'Pending'",nativeQuery = true)
    List<Services> fetchAllServicesHistoryApplicationsByDateRangeAndMemberNo(String fromDate, String toDate, Long memberNo);

    @Query(value = "SELECT a.id,f.farmer_no,  f.mobile_no as mobileNo, r.route as route,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join  product p on p.id =a.product_id and a.type = 'Service' and a.farmer_no= :memberNo and DATE(a.allocatio_date) between :fromDate and :toDate and a.service_status <> 'Pending'",nativeQuery = true)
    List<Services> fetchAllServicesHistoryApplicationsByDateRange(String fromDate, String toDate);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,  f.mobile_no as mobileNo, r.route as route,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join product p on p.id =a.product_id and a.type = 'Service' and YEAR(a.allocatio_date) = :year and MONTH(a.allocatio_date) = :month",nativeQuery = true)
    List<Services> fetchAllServicesApplicationMonthly(Integer year, Integer month);

    @Query(value = "SELECT a.id,f.farmer_no , f.mobile_no as mobileNo, r.route as route,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join  product p on p.id =a.product_id and a.type = 'Service' and a.farmer_no= :memberNo and a.service_status = 'Pending'",nativeQuery = true)
    List<Services> fetchAllOpenServicesApplicationsByFarmerNo(Long memberNo);

    @Query(value = "SELECT a.id,f.farmer_no,f.username,  f.mobile_no as mobileNo, r.route as route,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join product p on p.id =a.product_id and a.type = 'Service' and a.farmer_no= :memberNo and DATE(a.allocatio_date) between :fromDate and :toDate and a.service_status = 'Pending'",nativeQuery = true)
    List<Services> fetchAllOpenServicesApplicationsByDateRangeAndMemberNo(String fromDate, String toDate, Long memberNo);

    @Query(value = "SELECT a.id,f.farmer_no,f.username ,  f.mobile_no as mobileNo, r.route as route,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on r.id=f.route_fk join product p on p.id =a.product_id and a.type = 'Service' and DATE(a.allocatio_date) between :fromDate and :toDate and a.service_status = 'Pending'",nativeQuery = true)
    List<Services> fetchAllOpenServicesApplicationsByDateRange(String fromDate, String toDate);

    @Query(value = "SELECT a.id,f.farmer_no, f.mobile_no as mobileNo, r.route as route,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.status,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as  revokeStatus, a.service_status as serviceStatus, a.requested_on as requestedOn, a.resolved_on as resolvedOn, a.resolved_by as resolvedBy from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join route r on f.route_fk=r.id join product p on p.id =a.product_id and a.type = 'Service'",nativeQuery = true)
    List<Services> getAllServiceApplications();
    @Query(value = "SELECT a.id,f.farmer_no, f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocation_date) as allocationDate,CAST(a.allocation_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus,a.revoke_status as revokeStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and a.status ='APPROVED'",nativeQuery = true)
    List<Allocations> getAllocationsByFarmer(Integer farmer_no);
    @Query(value = "SELECT a.id,f.farmer_no,f.username,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date) as allocationDate,CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy ,a.payment_status as paymentStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no and  a.payment_status=:payment_status and a.status =:status",nativeQuery = true)
    List<Allocations> getAllocationsByFarmerByPaymentStatus(Integer farmer_no,Character payment_status,Character status);
    @Query(value = "SELECT f.username,f.farmer_no,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus  from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE DATE(a.allocation_date)=:date",nativeQuery = true)
    List<Allocations> getAllocationsByDate(String date);
    @Query(value = "SELECT a.username,f.farmer_no,a.no_of_cows as noOfCows,a.heat_start_date as heatstartDate,a.type,p.name as product,a.amount as amount,a.quantity as quantity,DATE(a.allocatio_date),CAST(a.allocatio_date as time) as time ,a.allocatedby as allocatedBy,a.payment_status as paymentStatus from farmer_product_allocations a join farmer f  on f.farmer_no=a.farmer_no join  product p on p.id =a.product_id WHERE a.farmer_no = :farmer_no  and DATE(a.allocatio_date)=:date",nativeQuery = true)
    List<Allocations> getFAllocationsPerDate(Integer farmer_no,String date);
    @Query(value = "SELECT SUM(fa.amount) as accruedamount,f.farmer_no,f.username  from farmer_product_allocations fa join farmer f on f.farmer_no =fa.farmer_no where DATE(fa.allocatio_date) BETWEEN :from and :to and f.farmer_no =:farmer_no and  fa.payment_status =:payment_status",nativeQuery = true)
    FarmerAllocationData getFAllocationsSummary(Integer farmer_no,String from ,String to,Character payment_status);


    interface FarmerAllocationData{
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
