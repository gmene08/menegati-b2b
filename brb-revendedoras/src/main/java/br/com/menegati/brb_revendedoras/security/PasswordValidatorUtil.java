package br.com.menegati.brb_revendedoras.security;

import lombok.experimental.UtilityClass;
import org.passay.*;

import java.util.Arrays;

@UtilityClass
public class PasswordValidatorUtil {

    private static final PasswordValidator validator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 30),              // Min 8, max 30 characters
            new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least 1 uppercase
            new CharacterRule(EnglishCharacterData.Special, 1),   // At least 1 special char
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new WhitespaceRule()
    ));

    public boolean isValid(String password) {
        return validator.validate(new PasswordData(password)).isValid();
    }
}
