package com.emtech.dairyapp.Dairy.Supply.returns;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilkReturnRepo extends JpaRepository<MilkReturns, Long> {
}
