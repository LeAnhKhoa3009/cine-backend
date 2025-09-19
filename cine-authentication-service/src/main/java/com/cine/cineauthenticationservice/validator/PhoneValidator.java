package com.cine.cineauthenticationservice.validator;

import java.util.regex.Pattern;

public class PhoneValidator {
    private static final String PHONE_NUMBER_PATTERN = "^[+]?[0-9]{10,15}$";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return pattern.matcher(phoneNumber).matches();
    }
}
