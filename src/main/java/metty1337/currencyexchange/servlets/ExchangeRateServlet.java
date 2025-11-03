package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.factory.ExchangeRateServiceFactory;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final String ERROR_404 = "Exchange Rate Not Found";
    private static final String RATE_ATTRIBUTE = "rate";
    private static final String BASE_CURRENCY_CODE_ATTRIBUTE = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_ATTRIBUTE = "targetCurrencyCode";
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        this.exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getAttribute(BASE_CURRENCY_CODE_ATTRIBUTE).toString();
        String targetCurrencyCode = request.getAttribute(TARGET_CURRENCY_CODE_ATTRIBUTE).toString();

        try {
            ExchangeRateDTO exchangeRateDTO = exchangeRateService.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);
            JsonManager.writeJsonResult(response, exchangeRateDTO);
        } catch (RuntimeException e) {
            handleErrors(e, response);
        }
    }


    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getAttribute(BASE_CURRENCY_CODE_ATTRIBUTE).toString();
        String targetCurrencyCode = request.getAttribute(TARGET_CURRENCY_CODE_ATTRIBUTE).toString();
        BigDecimal rate = new BigDecimal(request.getAttribute(RATE_ATTRIBUTE).toString()).setScale(6, RoundingMode.HALF_UP);
        try {
            ExchangeRateDTO exchangeRateDTO = exchangeRateService.changeRate(baseCurrencyCode, targetCurrencyCode, rate);
            response.setStatus(HttpServletResponse.SC_OK);
            JsonManager.writeJsonResult(response, exchangeRateDTO);
        } catch (RuntimeException e) {
            handleErrors(e, response);
        }
    }

    private void handleErrors(RuntimeException e, HttpServletResponse response) {
        if (e instanceof ExchangeRateDoesntExistException || e instanceof CurrencyDoesntExistException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log(ERROR_404);
            JsonManager.writeJsonError(response, ERROR_404);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log(ErrorMessages.ERROR_500.getMessage());
            JsonManager.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
        }
    }
}







