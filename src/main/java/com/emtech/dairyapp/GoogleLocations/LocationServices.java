package com.emtech.dairyapp.GoogleLocations;

import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LocationServices {


    @Value("${google.location.API-KEY}")
    private String apiKey;


    private final  String baseurl="https://maps.googleapis.com/maps/api/geocode";




    public ResponseEntity getLocations(String latitude, String longitude) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(baseurl+"/json?latlng="+latitude+","+longitude+"&key="+apiKey)
                .get()
                .addHeader("Content-Type",mediaType.charset().toString())
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
        return ResponseEntity.ok().body(response.body().string());
    }


}
