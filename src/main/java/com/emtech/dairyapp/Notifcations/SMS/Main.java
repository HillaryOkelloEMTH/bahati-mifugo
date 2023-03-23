package com.emtech.dairyapp.Notifcations.SMS;

public class Main {
    public static void main(String[] args) {
        SMSService service = new SMSService();
        service.sendSMS("Hi Ibrahim, Good morning ","254725634469");
    }
}
