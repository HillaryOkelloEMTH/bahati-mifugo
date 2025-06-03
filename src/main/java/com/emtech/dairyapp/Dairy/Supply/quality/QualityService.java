package com.emtech.dairyapp.Dairy.Supply.quality;

import com.emtech.dairyapp.Auth.User.User;
import com.emtech.dairyapp.Auth.User.UserRepository;
import com.emtech.dairyapp.Auth.User.UserService;
import com.emtech.dairyapp.Configurations.FarmerManagement.FarmerRepo;
import com.emtech.dairyapp.Configurations.Interfaces.FarmerData;
import com.emtech.dairyapp.Configurations.Utils.HttpClient;
import com.emtech.dairyapp.Configurations.Utils.SignatureService;
import com.emtech.dairyapp.Response.EntityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QualityService {
    @Value("${dairy.lactovate-secret}")
    String secretKey;

    private FarmerRepo farmerRepo;
    private SignatureService signatureService;
    private UserRepository userRepo;

    private HttpClient client;
    private String collector = "";
    private final ObjectMapper objectMapper = new ObjectMapper();


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
            payload.addProperty("phone", "");
            payload.addProperty("email", "");

            Headers headers = new Headers.Builder()
                    .add("X-Signature", signatureService.signData(payload.toString()))
                    .add("X-Client-ID", "emtech-dairy")
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
            User u = optional.get();

            payload.addProperty("name", u.getUsername());
            payload.addProperty("email", u.getEmail());
            payload.addProperty("agentNumber", u.getId());
            payload.addProperty("password", "1234");

            Headers headers = new Headers.Builder()
                    .add("X-Signature", signatureService.signData(payload.toString()))
                    .add("X-Client-ID", "1")
                    .build();

            EntityResponse<String> response = client.req("/api/partners/create-agent", headers, payload);

        } catch (Exception e) {
            log.error(e.toString());
        }
        return res;
    }


    public EntityResponse<?> getTestsLogs() {
        EntityResponse<?> res = new EntityResponse<>();
        JsonObject payload = new JsonObject();
        List<TestResDto> tests = new ArrayList<>();

        try {
            log.info("Retrieving test logs from lactovate");

            Headers headers = new Headers.Builder()
                    .add("Signature", signatureService.hmacSha256("", secretKey))
                    .build();

            EntityResponse<String> result = client.req("/api/partners/test-logs", headers, payload);

            if (!"200".equals(result.getStatusCode().toString())) {
                log.error("Failed to load test logs information");
                res.setMessage("Failed get test logs information");
                res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }

            TestResDto testsRes= objectMapper.readValue(result.getEntity(), TestResDto.class);

            System.out.println("Here are the tests logs "+ testsRes);

            res.setStatusCode(HttpStatus.OK.value());
            res.setMessage("Successful");
        } catch (Exception e) {
            log.error(e.toString());
            res.setMessage(e.getMessage());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return res;
    }

}
