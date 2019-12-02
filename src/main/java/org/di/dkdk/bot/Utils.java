package org.di.dkdk.bot;

import org.apache.commons.validator.EmailValidator;

public class Utils {

    public static boolean isValidateEmailAddress(String email){//для проверки правильности ввода емаила

        return EmailValidator.getInstance().isValid(email);
    }
}
