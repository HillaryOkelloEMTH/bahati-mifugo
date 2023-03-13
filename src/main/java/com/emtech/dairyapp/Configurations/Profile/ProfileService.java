package com.emtech.dairyapp.Configurations.Profile;

import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;

    EntityResponse response = new EntityResponse<>();





    public EntityResponse addProfile(Profile profile){

        log.info("saving profile....");
        try{
            Long size= profileRepo.count();
            if(size>1){
                response.setMessage("Profile has already been set");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(profile);
                return  response;
            }else {
                profile.setCreatedAt(new Date());
                profileRepo.save(profile);
                log.info("Profile Saved !");
                response.setMessage("Profile added.");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(profile);
                return response;
            }

        }catch (Exception e){
            log.info("error saving profile....");
            response.setMessage("Profile added.");
            response.setStatusCode(HttpStatus.OK.value());
            return  response;


        }
    }
    public EntityResponse getProfile(){

        log.info("getting profile....");
        try{

           List<Profile> profile= profileRepo.findAll();
            log.info("Profile Saved !");
            response.setMessage("Profile added.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(profile);
            return  response;

        }catch (Exception e){
            log.info("error fetching profile....");
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return  response;


        }
    }


    public EntityResponse updateProfile(Profile profile){

        log.info("updating profile....");
        try{

            profile.setCreatedAt(new Date());
            profileRepo.save(profile);
            log.info("Profile Saved !");
            response.setMessage("Profile updated.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(profile);
            return  response;

        }catch (Exception e){
            log.info("error updating profile....");
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return  response;


        }
    }


    public EntityResponse deleteProfile(Long id){

        log.info("deleting profile....");
        try{
           profileRepo.deleteById(id);
            log.info("Profile deleted !");
            response.setMessage("Profile deleted.");
            response.setStatusCode(HttpStatus.OK.value());
            return  response ;

        }catch (Exception e){
            log.info("Error deleting profile....");
            response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return  response;


        }
    }



}
