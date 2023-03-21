package com.emtech.dairyapp.Auth.Role;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByStatus(@NonNull Integer s);

    Optional<Role> findByName(String role_name);
    @Query(value = "select count(*) from  roles",nativeQuery = true)
    Integer countroles();
}
