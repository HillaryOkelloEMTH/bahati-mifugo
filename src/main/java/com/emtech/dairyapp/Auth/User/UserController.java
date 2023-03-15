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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;


@Log
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(
            path = "/create-user",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RecordCreateResponse> createUser(@RequestBody UserCreateRequest body){
        if(this.userService.createUser(body.getUsername(), body.getFirstName(), body.getLastName(), body.getEmail(), body.getMobile(), body.getRole())){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User created successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest body){
        log.log(Level.WARNING, String.format("User Credentials [credentials=%s]", body));
        AuthResponse authResponse = this.userService.authenticateUser(body.getUsername(), body.getPassword());

        if(authResponse != null){
            return ResponseEntity.ok().body(authResponse);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(
            path = "/update-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RecordCreateResponse> updateUser(@RequestBody UserCreateRequest body, @PathVariable Long userId){
        if(this.userService.updateUser(userId, body.getUsername(), body.getFirstName(), body.getLastName(), body.getEmail(), body.getRole())){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/lock-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<RecordCreateResponse> lockUser(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Locked")){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/unlock-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<RecordCreateResponse> unlockUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Active")){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/delete-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<RecordCreateResponse> deleteUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Deleted")){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/restore-user/{userId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<RecordCreateResponse> restoreUserAccount(@PathVariable Long userId){
        if(this.userService.updateUserStatus(userId, "Restore")){
            return ResponseEntity.ok().body(RecordCreateResponse.builder().message("User updated successfully !").build());
        }else {
            return ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build());
        }
    }

    @RequestMapping(
            path = "/all-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<UserResponse> fetchAllUserAccounts(){
        UserResponse users = this.userService.getAllUsers();

        if(users != null){
            return ResponseEntity.ok().body(users);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    @RequestMapping(
            path = "{userId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<UserData> getUserDetails(@PathVariable Long userId){
        UserData user = this.userService.getUserDetails(userId);

        if(user != null){
            return ResponseEntity.ok().body(user);
        }else {
            return ResponseEntity.notFound().build();
        }

    }

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

    @RequestMapping(
            path = "/locked-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<UserResponse> fetchAllLockedUserAccounts(){
        UserResponse users = this.userService.getUsersByStatus("Locked");

        if(users != null){
            return ResponseEntity.ok().body(users);
        }else {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping(
            path = "/deleted-accounts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<UserResponse> fetchAllDeletedUserAccounts(){
        UserResponse users = this.userService.getUsersByStatus("Deleted");

        if(users != null){
            return ResponseEntity.ok().body(users);
        }else {
            return ResponseEntity.notFound().build();
        }

    }


}
