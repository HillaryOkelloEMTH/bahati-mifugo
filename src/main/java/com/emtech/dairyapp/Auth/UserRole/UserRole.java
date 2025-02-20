package com.emtech.dairyapp.Auth.UserRole;

import com.emtech.dairyapp.Auth.Role.Role;
import com.emtech.dairyapp.Auth.User.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.sql.Timestamp;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "user_role_user"))
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "user_role_role"))
    private Role role;

    @Column(name = "status")
    private Integer status;

    @CreationTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "creation_date", nullable = false)
    private Timestamp creation_date;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd-MMM-yyyy HH:mm:ss")
    @Column(name = "update_date", nullable = false)
    private Timestamp update_date;
}
