package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.factory.CurrencyServiceFactory;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String ERROR_404_MESSAGE = "Currency Code Is Not Found";
    private static final String CODE_ATTRIBUTE = "code";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getAttribute(CODE_ATTRIBUTE).toString();
        CurrencyService currencyService = CurrencyServiceFactory.createCurrencyService();
        try {
            CurrencyDTO currencyDTO = currencyService.getCurrencyByCode(code);
            response.setStatus(HttpServletResponse.SC_OK);
            JsonManager.writeJsonResult(response, currencyDTO);
        } catch (RuntimeException e) {
            if (e instanceof CurrencyDoesntExistException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log(ERROR_404_MESSAGE, e);
                JsonManager.writeJsonError(response, ERROR_404_MESSAGE);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log(ErrorMessages.ERROR_500.getMessage(), e);
                JsonManager.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
            }
        }
    }
}
