package metty1337.currencyexchange.servlets;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyDoesntExistException;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.util.JsonResponseWriter;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String ERROR_404_MESSAGE = "Currency Code Is Not Found";
    private static final String CODE_ATTRIBUTE = "code";

    @Inject
    private CurrencyService currencyService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getAttribute(CODE_ATTRIBUTE).toString();
        try {
            CurrencyDTO currencyDTO = currencyService.getCurrencyByCode(code);
            response.setStatus(HttpServletResponse.SC_OK);
            JsonResponseWriter.writeJsonResult(response, currencyDTO);
        } catch (RuntimeException e) {
            if (e instanceof CurrencyDoesntExistException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log(ERROR_404_MESSAGE, e);
                JsonResponseWriter.writeJsonError(response, ERROR_404_MESSAGE);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log(ErrorMessages.ERROR_500.getMessage(), e);
                JsonResponseWriter.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
            }
        }
    }
}
