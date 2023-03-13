package com.emtech.dairyapp.Auth.Data.Http.Response.Auth;

import com.emtech.dairyapp.Auth.Converter.AccessRightsConverter;
import com.emtech.dairyapp.Auth.Data.Role.RoleAccessRights;
import com.emtech.dairyapp.Auth.Role.RoleService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleData implements Serializable {
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private String name = null;

    @Builder.Default
    private Integer status = null;

    @Builder.Default
    private Timestamp creationDate = null;

    @Builder.Default
    private Timestamp updateDate = null;

    @Builder.Default
    private List<RoleAccessRights> accessRights = null;
}
