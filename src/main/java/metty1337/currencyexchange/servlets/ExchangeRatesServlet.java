package metty1337.currencyexchange.servlets;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExchangeRateAlreadyExistsException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonResponseWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final String BASE_CURRENCY_CODE_PARAMETER = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_PARAMETER = "targetCurrencyCode";
    private static final String RATE_PARAMETER = "rate";
    private static final String ERROR_409 = "Exchange rate already exists";
    private static final String ERROR_404 = "One (or both) currencies from the currency pair do not exist in the database";

    @Inject
    private ExchangeRateService exchangeRateService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ExchangeRateDTO> exchangeRateDTOs = exchangeRateService.getExchangeRates();

            response.setStatus(HttpServletResponse.SC_OK);
            JsonResponseWriter.writeJsonResult(response, exchangeRateDTOs);
        } catch (DatabaseException e) {
            log(ErrorMessages.ERROR_500.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonResponseWriter.writeJsonResult(response, ErrorMessages.ERROR_500.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getAttribute(BASE_CURRENCY_CODE_PARAMETER).toString();
        String targetCurrencyCode = request.getAttribute(TARGET_CURRENCY_CODE_PARAMETER).toString();
        BigDecimal rate = new BigDecimal(request.getAttribute(RATE_PARAMETER).toString()).setScale(6, RoundingMode.HALF_UP);

        try {
            ExchangeRateDTO exchangeRateDTO = exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonResponseWriter.writeJsonResult(response, exchangeRateDTO);
        } catch (RuntimeException e) {
            if (e instanceof ExchangeRateAlreadyExistsException) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                log(ERROR_409);
                JsonResponseWriter.writeJsonError(response, ERROR_409);
            } else if (e instanceof ExchangeRateDoesntExistException || e instanceof CurrencyDoesntExistException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log(ERROR_404);
                JsonResponseWriter.writeJsonError(response, ERROR_404);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log(ErrorMessages.ERROR_500.getMessage(), e);
                JsonResponseWriter.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
            }
        }
    }
}