package com.emtech.dairyapp.Auth.Utilities;

import org.springframework.security.core.userdetails.UserDetails;

public class CurrentUserContext {
    private static final ThreadLocal<UserDetails> currentContext = new ThreadLocal<>();

    public static void setCurrentUserContext(UserDetails userContext) {currentContext.set(userContext);}
    public static UserDetails getCurrentUserContext() {return currentContext.get();}

    public static void clear() {currentContext.remove();}
}
