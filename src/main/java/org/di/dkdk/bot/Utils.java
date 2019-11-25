package org.di.dkdk.bot;

import org.apache.commons.validator.EmailValidator;

public class Utils {

    public static boolean isValidateEmailAddress(String email){
        return EmailValidator.getInstance().isValid(email);
    }
}
