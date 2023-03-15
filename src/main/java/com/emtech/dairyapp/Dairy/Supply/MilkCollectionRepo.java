package com.emtech.dairyapp.Dairy.Supply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilkCollectionRepo extends JpaRepository<MilkCollections,Long> {
}
