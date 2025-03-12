package com.emtech.dairyapp.Auth.Utilities;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
