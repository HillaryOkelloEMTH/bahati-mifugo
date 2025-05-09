package com.emtech.dairyapp.Auth.UserRole;

import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.User.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

//import jakarta.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(@NonNull User u, @NonNull Role r);

    Optional<UserRole> findByUser(@NonNull User u);

    List<UserRole> findAllByUser(@NonNull User u);

 List<UserRole> findAllByUserAndStatus(@NonNull User user,  Integer s);

 @NotNull List<UserRole> findAll();
}
