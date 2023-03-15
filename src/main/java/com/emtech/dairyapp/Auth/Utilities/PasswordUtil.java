package com.emtech.dairyapp.Auth.Utilities;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;


@Component
@Log
public class PasswordUtil implements PasswordEncoder {

    @Value("${jwt.password.encoder.secret}")
    private String passwordSecret;

    @Value("${jwt.password.encoder.iteration}")
    private Integer iteration;

    @Value("${jwt.password.encoder.keylength}")
    private Integer keylength;
    @Override
    public String encode(CharSequence cs) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                    .generateSecret(new PBEKeySpec(cs.toString().toCharArray(), passwordSecret.getBytes(), iteration, keylength))
                    .getEncoded();

            log.log(Level.INFO, String.format("Generated Password By Custom Password Encoder [ %s ]",  Base64.getEncoder().encodeToString(result)));

            return Base64.getEncoder().encodeToString(result);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return encode(cs).equals(string);
    }



}
