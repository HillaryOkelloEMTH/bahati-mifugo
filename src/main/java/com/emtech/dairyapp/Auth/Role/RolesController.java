package com.emtech.dairyapp.Auth.Role;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.RoleCreateRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RecordCreateResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RoleData;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RoleResponse;
import com.emtech.dairyapp.Auth.Data.Role.RoleAccessRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/admin/api/v1/roles")
public class RolesController {
    @Autowired
    RoleService roleService;


    @RequestMapping(
            path = "/access-rights",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<RoleAccessRights> fetchAllAccessRights(){
        return roleService.accessRights();
    }

//    @PreAuthorize(value = "hasAuthority('CREATE_ROLE')")
    @RequestMapping(
            path = "/create-role",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> createRole(
            @RequestBody RoleCreateRequest body
    ) {
        if (this.roleService.createRole(body.getName(), body.getAccessRightList())) {
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Role added successfully !").build()));
        } else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_ROLE')")
    @RequestMapping(
            path = "/update-role/{roleId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleCreateRequest body
    ) {
        if (this.roleService.updateRole(roleId, body.getName(), body.getAccessRightList())) {
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Role updated successfully !").build()));
        } else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_ROLE')")
    @RequestMapping(
            path = "/deactivate-role/{roleId}",
            method = RequestMethod.PUT,
//            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> deactivateRole(
            @PathVariable Long roleId
    ) {
        if (this.roleService.deactivateRole(roleId)) {
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Role deactivated successfully !").build()));
        } else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_ROLE')")
    @RequestMapping(
            path = "/activate-role/{roleId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>>  activateRole(
            @PathVariable Long roleId
    ) {
        if (this.roleService.activateRole(roleId)) {
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Role activated successfully !").build()));
        } else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }


//    @PreAuthorize(value = "hasAuthority('VIEW_ROLES')")
    @RequestMapping(
            path = "/all-roles",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RoleResponse>> getAllRoles() {
       RoleResponse roles = roleService.fetchAllRoles();

       if (roles != null){
           return Mono.just(ResponseEntity.ok().body(roles));
       }else {
           return Mono.just(ResponseEntity.notFound().build());
       }
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_ROLES')")
    @RequestMapping(
            path = "{roleId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RoleData>> getRoleDetails(@PathVariable Long roleId) {
        RoleData role = roleService.fetchRoleById(roleId);

        if (role != null){
            return Mono.just(ResponseEntity.ok().body(role));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_ROLES')")
    @RequestMapping(
            path = "/active-roles",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RoleResponse>> getAllActiveRoles() {
        RoleResponse roles = roleService.fetchRolesByStatus(1);

        if (roles != null){
            return Mono.just(ResponseEntity.ok().body(roles));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_ROLES')")
    @RequestMapping(
            path = "/inactive-roles",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RoleResponse>> getAllInactiveRoles() {
        RoleResponse roles = roleService.fetchRolesByStatus(0);

        if (roles != null){
            return Mono.just(ResponseEntity.ok().body(roles));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }
}
