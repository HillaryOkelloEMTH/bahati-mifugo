package com.emtech.dairyapp.Configurations.Profile;


import com.emtech.dairyapp.Response.EntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/v1/profile")
public class ProfileController {

    private  final ProfileService service;

    @PostMapping("create")
    public ResponseEntity<EntityResponse> createProfile(@RequestBody Profile profile){
        EntityResponse response = service.addProfile(profile);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("fetch")
    public ResponseEntity<EntityResponse> getProfile(){
        EntityResponse response = service.getProfile();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("update")
    public ResponseEntity<EntityResponse> updateProfile(@RequestBody Profile profile){
        EntityResponse response = service.updateProfile(profile);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<EntityResponse> updateProfile(@PathVariable Long id){
        EntityResponse response = service.deleteProfile(id);
        return ResponseEntity.ok().body(response);
    }


}
