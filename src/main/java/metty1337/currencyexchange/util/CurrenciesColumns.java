package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CurrenciesColumns {
    ID("ID"),
    CODE("Code"),
    NAME("FullName"),
    SIGN("Sign");
    private final String columnName;
}











//public final class CurrenciesColumns {
//    public static final String ID = "ID";
//    public static final String CODE = "Code";
//    public static final String NAME = "FullName";
//    public static final String SIGN = "Sign";
//}
