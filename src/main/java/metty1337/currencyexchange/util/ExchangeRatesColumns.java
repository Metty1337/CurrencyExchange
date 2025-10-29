package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExchangeRatesColumns {
    ID("ID"),
    BASE_CURRENCY_ID("BaseCurrencyId"),
    TARGET_CURRENCY_ID("TargetCurrencyId"),
    RATE("Rate");
    private final String columnName;
}
