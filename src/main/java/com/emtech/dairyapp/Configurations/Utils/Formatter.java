package com.emtech.dairyapp.Configurations.Utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class Formatter {
        public static String formatPhone(String phone) {
            if (phone.startsWith("0")) {
                log.info("Starting with 0");
                phone = phone.replaceFirst("0", "254");
            } else if (phone.startsWith("+")) {
                log.info("Starting with +");
                phone = phone.substring(1);
            } else if (phone.startsWith("7") || phone.startsWith("1")) {
                phone = "254" + phone;
            }
            return phone;
        }

        public static String formatDate(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

            // Set the timezone to EAT (East Africa Time)
            TimeZone eatTimeZone = TimeZone.getTimeZone("Africa/Nairobi");
            dateFormat.setTimeZone(eatTimeZone);

            // Format the date
            return dateFormat.format(date);
        }
}
