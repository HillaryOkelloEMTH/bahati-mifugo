package com.emtech.dairyapp.Configurations.Utils;

import com.emtech.dairyapp.Response.EntityResponse;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class HttpClient {
    @Value("${dairy.quality-test-url}")
    private String baseUrl;

    private final OkHttpClient client = new OkHttpClient();


    public EntityResponse<String> req(String method, String endpoint, Headers reqHeaders, JsonObject payload) {
        EntityResponse<String> response = new EntityResponse<>();

        log.info("initializing request, sending to quality check server");
        try {
            RequestBody req = RequestBody.create(String.valueOf(payload), MediaType.parse("application/json"));
            Headers headers = new Headers.Builder()
                    .addAll(reqHeaders)
                    .add("Content-Type", "application/json")
                    .add("serviceid", "testserviceid123456")
                    .build();

            Request request;
            if (method.equals("POST")) {
                request = new Request.Builder().url(baseUrl+endpoint)
                        .post(req)
                        .headers(headers).build();
            } else {
                request = new Request.Builder().url(baseUrl+endpoint)
                        .get().headers(headers).build();
            }

            try(Response res = client.newCall(request).execute()) {
                System.out.println("The response is "+res.body().string());
                if (res.isSuccessful()) {
                    String resBody = "";
                    if (res.body() != null) {
                        resBody = res.body().string();
                    }

                    log.info("Request is successful {}", res);
                    response.setMessage("ok");
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(resBody);
                } else {
                    log.info("Error from request {} and body {}", res.message(),res.body());

                    response.setMessage(res.message());
                    response.setStatusCode(res.code());
                    response.setEntity("error");
                }

                return response;
            } catch (IOException e) {
                log.error(e.toString());

                response.setMessage(e.getMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setEntity("error");
            }
        } catch (Exception e) {
            log.error("Error Caught: {}",e.toString());

            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("error");
        }
        return response;
    }
}
