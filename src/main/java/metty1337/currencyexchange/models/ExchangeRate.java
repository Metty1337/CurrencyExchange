package metty1337.currencyexchange.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ExchangeRate {
    private Integer ID;
    private Integer BaseCurrencyID;
    private Integer TargetCurrencyID;
    private double Rate;
}
