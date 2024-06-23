package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.ForgotPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.ResetPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.UpdateUserPasswordRequest;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.AuthResponse;
import com.emtech.dairyapp.Auth.Data.Http.Response.Auth.RecordCreateResponse;
import com.emtech.dairyapp.Response.EntityResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.logging.Level;

@Log
@CrossOrigin("*")
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
    public Mono<ResponseEntity<EntityResponse<AuthResponse>>> login(@RequestBody AuthRequest body){
        log.log(Level.WARNING, String.format("User Credentials [credentials=%s]", body));
        EntityResponse<AuthResponse> authResponse = this.userService.authenticateUser(body.getUsername(), body.getPassword());

        if(authResponse != null){
            return Mono.just(ResponseEntity.status(authResponse.getStatusCode()).body(authResponse));
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
        RecordCreateResponse response = this.userService.updateUserPassword(body.getUsername(), body.getPreviousPassword(), body.getPassword());
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }

    @RequestMapping(
            path = "/forgot-password",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> forgotPassword(@RequestBody ForgotPasswordRequest body){
        RecordCreateResponse response;

        if(body.getUsername() != null && !body.getUsername().isEmpty()){
            response = this.userService.forgotPassword(body.getUsername().trim());
//            if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
//                response = RecordCreateResponse.builder().message("Password reset token requested successfully !").build();
//            }else {
//                re = RecordCreateResponse.builder().message("Sorry, an error occurred").build();
//            }
        } else if (body.getEmail() != null && !body.getEmail().isEmpty()) {
            response = this.userService.resetPasswordTokenRequestedByEmail(body.getEmail().trim());
//            if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
//                message = RecordCreateResponse.builder().message("Password reset token requested successfully !").build();
//            }else {
//                message = RecordCreateResponse.builder().message("Sorry, an error occurred").build();
//            }
        } else if (body.getMobile() != null && !body.getMobile().isEmpty()) {
            response = this.userService.resetPasswordTokenRequestedByMobile(body.getMobile().trim());
//            if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())){
//                message = RecordCreateResponse.builder().message("Password reset token requested successfully !").build();
//            }else {
//                message = RecordCreateResponse.builder().message("Sorry, an error occurred").build();
//            }
        }else{
            response = RecordCreateResponse.builder().message("Sorry, an error occurred").build();
        }

        return Mono.just(ResponseEntity.ok().body(response));
    }

    @RequestMapping(
            path = "/reset-password",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public Mono<ResponseEntity<RecordCreateResponse>> resetPassword(@RequestBody ResetPasswordRequest body){
        RecordCreateResponse response = this.userService.resetPassword(body.getResetPasswordToken(), body.getPassword());
        if(!Objects.equals(response.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) && !response.getMessage().isEmpty()){
            return Mono.just(ResponseEntity.ok().body(response));
        }else {
            return Mono.just(ResponseEntity.internalServerError().body(response));
        }
    }
}
