package com.emtech.dairyapp.Auth.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@NonNull String username);

    Optional<User> findByEmail(@NonNull String e);

    List<User> findByStatus(@NonNull String status);

    @Query(value = "select count(*) from  users",nativeQuery = true)
    Integer countUsers();
}
