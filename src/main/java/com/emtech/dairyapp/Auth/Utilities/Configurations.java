package com.emtech.dairyapp.Auth.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Configurations {
    Properties prop;

    public Configurations() {

    }

    public Properties getProperties() {
        prop = new Properties();
        try {
            InputStream url = getClass().getClassLoader().getResourceAsStream("application.properties");
            prop.load(url);
        } catch (IOException asd) {
            System.out.println(asd.getMessage());
        }
        return prop;
    }
}
