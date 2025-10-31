package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.factory.ExchangeRateServiceFactory;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;

@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final String ERROR_400 = "Currency Code Is Missing at the address";
    private static final String ERROR_404 = "Exchange Rate Not Found";
    private static final String PARAMETER_RATE = "rate";
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        this.exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        JsonManager.prepareResponse(response);

        String input = request.getPathInfo();
        String baseCurrencyCode = getBaseCurrencyCode(input);
        String targetCurrencyCode = getTargetCurrencyCode(input);

        if (!isCodesValid(baseCurrencyCode, targetCurrencyCode)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log(ERROR_400);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
            try {
                ExchangeRateDTO exchangeRateDTO = exchangeRateService.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
                response.setStatus(HttpServletResponse.SC_OK);
                JsonManager.writeJsonResult(response, exchangeRateDTO);
            } catch (RuntimeException e) {
                handleErrors(e, response);
            }
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {
        JsonManager.prepareResponse(response);

        String inputCodes = request.getPathInfo();
        String baseCurrencyCode = getBaseCurrencyCode(inputCodes);
        String targetCurrencyCode = getTargetCurrencyCode(inputCodes);
        double rate = parseRateRequest(request.getParameter(PARAMETER_RATE));

        if (!isExchangeRateComponentsValid(baseCurrencyCode, targetCurrencyCode, rate)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log(ERROR_400);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
            try {
                ExchangeRateDTO exchangeRateDTO = exchangeRateService.changeRate(baseCurrencyCode, targetCurrencyCode, rate);
                response.setStatus(HttpServletResponse.SC_OK);
                JsonManager.writeJsonResult(response, exchangeRateDTO);
            } catch (RuntimeException e) {
                handleErrors(e, response);
            }
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

    private String getBaseCurrencyCode(String input) {
        if (isInputValid(input)) {
            return input.substring(1, 4);
        }
        return "";
    }

    private String getTargetCurrencyCode(String input) {
        if (isInputValid(input)) {
            return input.substring(4);
        }
        return "";
    }

    private boolean isInputValid(String input) {
        return input != null && input.length() > 6;
    }

    private boolean isCodesValid(String baseCurrencyCode, String targetCurrencyCode) {
        return !baseCurrencyCode.isBlank() && !targetCurrencyCode.isBlank();
    }

    private double parseRateRequest(String rate) {
        try {
            return Double.parseDouble(rate);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0;
        }
    }

    private boolean isExchangeRateComponentsValid(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        return baseCurrencyCode != null && targetCurrencyCode != null && rate != 0.0 && !baseCurrencyCode.isBlank() && !targetCurrencyCode.isBlank();
    }
}