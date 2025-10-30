package metty1337.currencyexchange.factory;

import jdk.jshell.spi.SPIResolutionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dao.ExchangeRateDAO;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.service.ExchangeRateService;

import java.io.PipedReader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExchangeRateServiceFactory {
    public static ExchangeRateService createExchangeRateService() {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        CurrencyMapper currencyMapper = new CurrencyMapper();
        CurrencyService currencyService = new CurrencyService(currencyDAO, currencyMapper);
        return new ExchangeRateService(exchangeRateDAO, currencyService);
    }
}
