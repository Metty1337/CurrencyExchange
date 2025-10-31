package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.ExchangeDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.factory.ExchangeRateServiceFactory;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private static final String PARAMETER_BASE_CURRENCY_CODE = "from";
    private static final String PARAMETER_TARGET_CURRENCY_CODE = "to";
    private static final String PARAMETER_AMOUNT = "amount";
    private static final String ERROR_400 = "A required field is missing";
    private static final String ERROR_404 = "One (or both) currencies from the currency pair do not exist in the database";
    private ExchangeRateService exchangeRateService;

    @Override
    public void init(){
        this.exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonManager.prepareResponse(response);

        String baseCurrencyCode = request.getParameter(PARAMETER_BASE_CURRENCY_CODE);
        String targetCurrencyCode = request.getParameter(PARAMETER_TARGET_CURRENCY_CODE);
        double amount = parseRateRequest(request.getParameter(PARAMETER_AMOUNT));

        if (!isRequestValid(baseCurrencyCode, targetCurrencyCode, amount)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log(ERROR_400);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
            try {
                ExchangeDTO exchangeDTO = exchangeRateService.exchange(baseCurrencyCode, targetCurrencyCode, amount);
                response.setStatus(HttpServletResponse.SC_OK);
                JsonManager.writeJsonResult(response, exchangeDTO);
            } catch (RuntimeException e) {
                handleErrors(e, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

    private double parseRateRequest(String rate) {
        try {
            return Double.parseDouble(rate);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0;
        }
    }

    private boolean isRequestValid(String baseCurrencyCode, String targetCurrencyCode, Double amount) {
        return baseCurrencyCode != null && targetCurrencyCode != null && amount != 0.0 && !baseCurrencyCode.isBlank() && !targetCurrencyCode.isBlank();
    }
}