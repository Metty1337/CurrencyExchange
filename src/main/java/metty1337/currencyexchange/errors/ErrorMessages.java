package metty1337.currencyexchange.errors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorMessages {
    ERROR_500("Data Base is unavailable");
    private final String message;
}
