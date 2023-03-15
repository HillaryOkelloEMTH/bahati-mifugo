/*
 * Copyright (c) 2022. Omukubwa Software Solutions (OSS)
 */

package com.emtech.dairyapp.Auth.Utilities;

import java.util.Random;

public class ToolKit {

    //Generate random java alphanumeric string
    public String generatePassword() {
        String characters = "01234ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz56789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < 8) {
            int index = (int) (rnd.nextFloat() * characters.length());
            sb.append(characters.charAt(index));
        }
        String s = sb.toString();
        return s;
    }

    //Generate Password reset token
    public int generatePassRessetToken()
    {
        Random rn = new Random();
        int token = rn.nextInt(1000000 - 110 + 1) + 2;
        return token;
    }
}
