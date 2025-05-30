package com.emtech.dairyapp.Dairy.Supply.quality;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Auth.User.UserService;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerData;
import com.emtech.dairyapp.Configurations.Utils.HttpClient;
import com.emtech.dairyapp.Configurations.Utils.SignatureService;
import com.emtech.dairyapp.Response.EntityResponse;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QualityService {
    private FarmerRepo farmerRepo;
    private SignatureService signatureService;
    private UserRepository userRepo;

    private HttpClient client;
    private String collector = "";


    public EntityResponse<?> postFarmer(Integer farmerNo) {
        JsonObject payload = new JsonObject();
        EntityResponse<?> res = new EntityResponse<>();
        try {

            Optional<FarmerData> optional = farmerRepo.getFarmerData(farmerNo);

            if (optional.isEmpty()) {
                log.error("Farmer with farmer no {} not found", farmerNo);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                return res;
            }
            FarmerData f = optional.get();
            collector = f.getCollector();

            payload.addProperty("name", f.getUsername());
            payload.addProperty("farmerNumber", f.getFno());
            payload.addProperty("region", f.getRoute());
            payload.addProperty("status", true);

            Headers headers = new Headers.Builder()
                    .add("X-Signature", signatureService.signData(payload.toString()))
                    .add("X-Client-ID", "1")
                    .build();

            EntityResponse<String> response = client.req("/api/farmers", headers, payload);

        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }

    public EntityResponse<?> postAgent() {
        JsonObject payload = new JsonObject();
        EntityResponse<?> res = new EntityResponse<>();
        try {

            Optional<User> optional = userRepo.findByUsername(collector);

            if (optional.isEmpty()) {
                log.error("Milk collector with username {} absent.", collector);
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                return res;
            }
            User f = optional.get();;

            payload.addProperty("name", f.getUsername());
            payload.addProperty("email", f.getEmail());
            payload.addProperty("phone", f.getMobile());
            payload.addProperty("password", "");

            Headers headers = new Headers.Builder()
                    .add("X-Signature", signatureService.signData(payload.toString()))
                    .add("X-Client-ID", "1")
                    .build();

            EntityResponse<String> response = client.req("/api/agents", headers, payload);

        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }
}
