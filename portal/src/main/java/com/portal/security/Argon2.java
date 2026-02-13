package com.portal.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public final class Argon2 {

    private static Argon2PasswordEncoder arg2SpringSecurity;
    private static boolean initialized;

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            arg2SpringSecurity = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        }
    }

    public static String hash(String password) {
        return arg2SpringSecurity.encode(password);
    }

    public static boolean verify(String hash, String password) {
        return arg2SpringSecurity.matches(password, hash);
    }

}
