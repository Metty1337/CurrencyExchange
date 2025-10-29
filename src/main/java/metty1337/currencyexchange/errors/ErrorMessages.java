package metty1337.currencyexchange.errors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorMessages {
    ERROR_500("Internal Server Error");
    private final String message;
}
