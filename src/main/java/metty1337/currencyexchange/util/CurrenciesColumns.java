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