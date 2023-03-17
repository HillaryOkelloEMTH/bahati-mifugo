package com.emtech.dairyapp.Auth.User;

import com.emtech.dairyapp.Auth.Data.Http.Request.Auth.AuthRequest;
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
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(path = "/api/v1/authentication")
public class AuthController {
    @Autowired
    UserService userService;

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
}
