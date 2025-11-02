package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidatorManager {
    private static final String NAME_REGEX = "^(.+)";
    private static final String CODE_REGEX = "([A-Z]{3})";
    private static final String SIGN_REGEX = "(.)";
    private static final String CODE_REQUEST_REGEX = "^/[A-Z]{3}$";
    private static final String CODES_REQUEST_REGEX = "^/[A-Z]{6}$";

    public static boolean isCurrencyComponentsValid(String name, String code, String sign) {
        return name.matches(NAME_REGEX) && code.matches(CODE_REGEX) && sign.matches(SIGN_REGEX);
    }

    public static boolean isCurrencyCodeRequestValid(String code) {
        return code.matches(CODE_REQUEST_REGEX);
    }

    public static boolean isCurrencyCodesRequestValid(String code) {
        return code.matches(CODES_REQUEST_REGEX);
    }

    public static boolean isRateInputValid(String input){
        return input != null && !input.isEmpty();
    }
}
