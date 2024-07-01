package com.emtech.dairyapp.Configurations.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormatPhoneNumber {
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
}
