package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.ForgotPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.ResetPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UpdateUserPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RecordCreateResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/authentication")
public class AuthController {
    @Autowired
    UserService userService;

//    @CrossOrigin(value = { "http://localhost:4200"}, allowedHeaders = {"Access-Control-Allow-Origin: *"})
    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest body){
        log.log(Level.WARNING, String.format("User Credentials [credentials=%s]", body));
        AuthResponse authResponse = this.userService.authenticateUser(body.getUsername(), body.getPassword());

        if(authResponse != null){
            return Mono.just(ResponseEntity.ok().body(authResponse));
        }else {
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }


    @RequestMapping(
            path = "/update-user-password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> updateUserPassword(@RequestBody UpdateUserPasswordRequest body){
        if(this.userService.updateUserPassword(body.getUsername(), body.getPassword())){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("User password updated successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

    @RequestMapping(
            path = "/forgot-password",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> forgotPassword(@RequestBody ForgotPasswordRequest body){
        if(this.userService.forgotPassword(body.getUsername())){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Password reset token requested successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }

    @RequestMapping(
            path = "/reset-password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> resetPassword(@RequestBody ResetPasswordRequest body){
        if(this.userService.resetPassword(body.getResetPasswordToken(), body.getPassword())){
            return Mono.just(ResponseEntity.ok().body(RecordCreateResponse.builder().message("Password has been changed successfully !").build()));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(RecordCreateResponse.builder().message("Sorry, an error occurred").build()));
        }
    }
}
