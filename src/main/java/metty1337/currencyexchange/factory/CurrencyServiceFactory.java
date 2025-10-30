package metty1337.currencyexchange.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.service.CurrencyService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CurrencyServiceFactory {
    public static CurrencyService createCurrencyService() {
        CurrencyMapper currencyMapper = new CurrencyMapper();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        return new CurrencyService(currencyDAO, currencyMapper);
    }
}

