package metty1337.currencyexchange.servlets;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.ExchangeDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.exceptions.ExchangeRateDoesntExistException;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonResponseWriter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private static final String PARAMETER_BASE_CURRENCY_CODE = "from";
    private static final String PARAMETER_TARGET_CURRENCY_CODE = "to";
    private static final String PARAMETER_AMOUNT = "amount";
    private static final String ERROR_404 = "One (or both) currencies from the currency pair do not exist in the database";

    @Inject
    private ExchangeRateService exchangeRateService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        String baseCurrencyCode = request.getAttribute(PARAMETER_BASE_CURRENCY_CODE).toString();
        String targetCurrencyCode = request.getAttribute(PARAMETER_TARGET_CURRENCY_CODE).toString();
        BigDecimal amount = new BigDecimal(request.getAttribute(PARAMETER_AMOUNT).toString()).setScale(6, RoundingMode.HALF_UP);

        try {
            ExchangeDTO exchangeDTO = exchangeRateService.exchange(baseCurrencyCode, targetCurrencyCode, amount);
            response.setStatus(HttpServletResponse.SC_OK);
            JsonResponseWriter.writeJsonResult(response, exchangeDTO);
        } catch (RuntimeException e) {
            handleErrors(e, response);
        }

    }

    private void handleErrors(RuntimeException e, HttpServletResponse response) {
        if (e instanceof ExchangeRateDoesntExistException || e instanceof CurrencyDoesntExistException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log(ERROR_404);
            JsonResponseWriter.writeJsonError(response, ERROR_404);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log(ErrorMessages.ERROR_500.getMessage());
            JsonResponseWriter.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
        }
    }
}