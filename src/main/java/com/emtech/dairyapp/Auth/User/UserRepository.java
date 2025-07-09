package com.emtech.dairyapp.Auth.User;

import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@NonNull String username);
    //for reset password
    Optional<User> findByResetPasswordToken(@NonNull String resetPasswordToken);

    Optional<User> findByEmail(@NonNull String e);

    Optional<User> findByMobile(@NonNull String mobile);

    List<User> findByStatus(@NonNull String status);

    @Query(value = "select count(*) from  users",nativeQuery = true)
    Integer countUsers();

    @Query(value = "select count(*) from  users where status != 'Active'",nativeQuery = true)
    Integer inactiveUsers();

    @Query(value="select * from users u join user_role ur on u.id=ur.user where ur.role= :roleId", nativeQuery = true)
    List<User> usersByRole(Long roleId);
}