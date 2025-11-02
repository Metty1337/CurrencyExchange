package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyAlreadyExistsException;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.factory.CurrencyServiceFactory;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.util.JsonManager;

import java.util.List;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_SIGN = "sign";
    private static final String ERROR_409 = "Currency with this code already exists";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        CurrencyService currencyService = CurrencyServiceFactory.createCurrencyService();
        try {
            List<CurrencyDTO> currenciesDTO = currencyService.getCurrencies();

            response.setStatus(HttpServletResponse.SC_OK);
            JsonManager.writeJsonResult(response, currenciesDTO);
        } catch (DatabaseException e) {
            log(ErrorMessages.ERROR_500.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonManager.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getAttribute(PARAMETER_NAME).toString();
        String code = request.getAttribute(PARAMETER_CODE).toString();
        String sign = request.getAttribute(PARAMETER_SIGN).toString();

        CurrencyService currencyService = CurrencyServiceFactory.createCurrencyService();
        CurrencyDTO currencyDTO = new CurrencyDTO(null, name, code, sign);
        try {
            currencyService.createCurrency(currencyDTO);
            currencyDTO = currencyService.getCurrencyByCode(currencyDTO.getCode());
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonManager.writeJsonResult(response, currencyDTO);
        } catch (RuntimeException e) {
            if (e instanceof CurrencyAlreadyExistsException) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                log(ERROR_409, e);
                JsonManager.writeJsonError(response, ERROR_409);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log(ErrorMessages.ERROR_500.getMessage(), e);
                JsonManager.writeJsonError(response, ErrorMessages.ERROR_500.getMessage());
            }
        }
    }
}
