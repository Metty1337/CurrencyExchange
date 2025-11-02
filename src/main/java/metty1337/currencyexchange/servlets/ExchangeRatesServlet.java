package metty1337.currencyexchange.servlets;

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
import metty1337.currencyexchange.factory.ExchangeRateServiceFactory;
import metty1337.currencyexchange.models.ExchangeRate;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final String PARAMETER_BASE_CURRENCY_CODE = "baseCurrencyCode";
    private static final String PARAMETER_TARGET_CURRENCY_CODE = "targetCurrencyCode";
    private static final String PARAMETER_RATE = "rate";
    private static final String ERROR_400 = "A required field is missing";
    private static final String ERROR_409 = "Exchange rate already exists";
    private static final String ERROR_404 = "One (or both) currencies from the currency pair do not exist in the database";

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        this.exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        JsonManager.prepareResponse(response);

        try {
            List<ExchangeRateDTO> exchangeRateDTOs = exchangeRateService.getExchangeRates();

            response.setStatus(HttpServletResponse.SC_OK);
            JsonManager.writeJsonResult(response, exchangeRateDTOs);
        } catch (DatabaseException e) {
            log(ErrorMessages.ERROR_500.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonManager.writeJsonResult(response, ErrorMessages.ERROR_500.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonManager.prepareResponse(response);

        String baseCurrencyCode = request.getParameter(PARAMETER_BASE_CURRENCY_CODE);
        String targetCurrencyCode = request.getParameter(PARAMETER_TARGET_CURRENCY_CODE);
        BigDecimal rate = parseRateRequest(request.getParameter(PARAMETER_RATE));

        if (!isExchangeRateComponentsValid(baseCurrencyCode, targetCurrencyCode, rate)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log(ERROR_400);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
            try {
                ExchangeRateDTO exchangeRateDTO = exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonManager.writeJsonResult(response, exchangeRateDTO);
            } catch (RuntimeException e) {
                if (e instanceof ExchangeRateAlreadyExistsException) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    log(ERROR_409);
                    JsonManager.writeJsonError(response, ERROR_409);
                } else if (e instanceof ExchangeRateDoesntExistException || e instanceof CurrencyDoesntExistException) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    log(ERROR_404);
                    JsonManager.writeJsonError(response, ERROR_404);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    log(ErrorMessages.ERROR_500.getMessage(), e);
                    JsonManager.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
                }
            }
        }

    }

    private BigDecimal parseRateRequest(String rate) {
        try {
            return new BigDecimal(rate);
        } catch (NumberFormatException | NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    private boolean isExchangeRateComponentsValid(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return baseCurrencyCode != null && targetCurrencyCode != null && (!(rate.compareTo(BigDecimal.ZERO) == 0)) && !baseCurrencyCode.isBlank() && !targetCurrencyCode.isBlank();
    }
}