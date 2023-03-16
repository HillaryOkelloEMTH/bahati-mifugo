package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UserCreateRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RecordCreateResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.UserResponse;
import com.emtech.dairyapp.Auth.Data.User.UserData;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;


@Log
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/admin/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;

//    @PreAuthorize(value = "hasAuthority('CREATE_USER')")
    @RequestMapping(
            path = "/create-user",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> createUser(@RequestBody UserCreateRequest body){
        if(this.userService.createUser(body.getUsername(), body.getFirstName(), body.getLastName(), body.getEmail(), body.getMobile(), body.getRole())){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User created successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/update-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> updateUser(@RequestBody UserCreateRequest body, @PathVariable Long userId){
        if(this.userService.updateUser(userId, body.getUsername(), body.getFirstName(), body.getLastName(), body.getEmail(), body.getRole())){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/lock-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> lockUser(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Locked")){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/unlock-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<RecordCreateResponse>> unlockUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Active")){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('DELETE_ROLE')")
    @RequestMapping(
            path = "/delete-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> deleteUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Deleted")){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

//    @PreAuthorize(value = "hasAuthority('UPDATE_USER')")
    @RequestMapping(
            path = "/restore-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> restoreUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Restore")){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

    @PreAuthorize(value = "hasAuthority('VIEW_USERS')")
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
