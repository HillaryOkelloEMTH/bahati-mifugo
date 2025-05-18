package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AdminUpdateUserPassword;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UpdateUserRoleRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UserCreateRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UserUpdateRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RecordCreateResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.UserResponse;
import com.emtech.dairyapp.Auth.Data.User.UserData;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Objects;


@Log
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/admin/api/v1/users")
public class UserController {
    @Autowired
    @Lazy
    UserService userService;

//    @PreAuthorize(value = "hasAuthority('CREATE_USER')")
    @RequestMapping(
            path = "/create-user",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> createUser(@RequestBody UserCreateRequest body){
        RecordCreateResponse response = this.userService.createUser(body.getUsername(), body.getFirstName(), body.getLastName(), body.getEmail(), body.getMobile(), body.getRole());

        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/update-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest body){
        RecordCreateResponse response = this.userService.updateUser(userId, body.getFirstName(), body.getFirstName());
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/update-user-role",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> updateUserRole(@RequestBody UpdateUserRoleRequest body){
        RecordCreateResponse response = this.userService.updateUserRole(body.getUsername(), body.getRoleId());
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    //    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/update-user-password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> updateUserPassword(@RequestBody AdminUpdateUserPassword body){
        RecordCreateResponse response = this.userService.adminUpdateUserPassword(body.getUsername(), body.getPassword());
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/lock-user/{userId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> lockUser(@PathVariable Long userId){
        RecordCreateResponse response = this.userService.updateUserStatus(userId, "Locked");
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/unlock-user/{userId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> unlockUserAccount(@PathVariable Long userId){
        RecordCreateResponse response = this.userService.updateUserStatus(userId, "Active");
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('DELETE_ROLE')")
    @RequestMapping(
            path = "/delete-user/{userId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> deleteUserAccount(@PathVariable Long userId){
        RecordCreateResponse response = this.userService.updateUserStatus(userId, "Deleted");
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/restore-user/{userId}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> restoreUserAccount(@PathVariable Long userId){
        RecordCreateResponse response = this.userService.updateUserStatus(userId, "Active");
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
    @RequestMapping(
            path = "/all-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<UserResponse>> fetchAllUserAccounts(){
        UserResponse users = this.userService.getAllUsers();

        if(users != null){
            return Mono.just(ResponseEntity.ok().body(users));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }

    }

    @GetMapping("by-role/{roleId}")
    public ResponseEntity<?> getUsersByRole(@PathVariable Long roleId) {
        var response = userService.getUsersByRole(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
    @RequestMapping(
            path = "{userId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<UserData>> getUserDetails(@PathVariable Long userId){
        UserData user = this.userService.getUserDetails(userId);

        if(user != null){
            return Mono.just(ResponseEntity.ok().body(user));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }

    }

//    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
    @RequestMapping(
            path = "/active-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<UserResponse>> fetchAllActiveUserAccounts(){
        UserResponse users = this.userService.getUsersByStatus("Active");

        if(users != null){
            return Mono.just(ResponseEntity.ok().body(users));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }

    }

//    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
    @RequestMapping(
            path = "/locked-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<UserResponse>> fetchAllLockedUserAccounts(){
        UserResponse users = this.userService.getUsersByStatus("Locked");

        if(users != null){
            return Mono.just(ResponseEntity.ok().body(users));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

//    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
    @RequestMapping(
            path = "/deleted-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<UserResponse>> fetchAllDeletedUserAccounts(){
        UserResponse users = this.userService.getUsersByStatus("Deleted");

        if(users != null){
            return Mono.just(ResponseEntity.ok().body(users));
        }else {
            return Mono.just(ResponseEntity.notFound().build());
        }

    }


}
