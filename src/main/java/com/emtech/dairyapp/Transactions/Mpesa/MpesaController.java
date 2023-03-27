package com.emtech.dairyapp.Transactions.Mpesa;

import com.emtech.dairyapp.Transactions.Data.Http.Request.B2CRequest;
import com.emtech.dairyapp.Transactions.Data.Http.Request.InitiateSTKPushRequest;
import com.emtech.dairyapp.Transactions.Data.Http.Response.B2CResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.STKPushResponse;
import com.google.gson.Gson;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.logging.Level;


@Log
@RestController
@RequestMapping(
        path = "/api/v1/transactions"
)
public class MpesaController {

    @Autowired
    MpesaService mpesaService;

    @RequestMapping(
            path = "/stk-push",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public STKPushResponse initiateSTKPush(@RequestBody InitiateSTKPushRequest body){
        STKPushResponse response = mpesaService.initiateSTKPush(body.getAmount(), body.getPhoneNumber());

        return response;
    }

    @RequestMapping(
            path = "/b2c",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public B2CResponse initiateB2C(@RequestBody B2CRequest body){
        B2CResponse response = mpesaService.initiateB2CRequest(body.getAmount(), body.getPhoneNumber());

        return response;
    }

    @RequestMapping(
            path = "/callback",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void stkPushCallback(@RequestBody Object object){
        this.mpesaService.processSTKPushCallBack(object);
    }


}
