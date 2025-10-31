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

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_SIGN = "sign";
    private static final String ERROR_400 = "A required field is missing";
    private static final String ERROR_409 = "Currency with this code already exists";

    @Override
    public void init() {
        this.currencyService = CurrencyServiceFactory.createCurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        JsonManager.prepareResponse(response);

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
        JsonManager.prepareResponse(response);

        String name = request.getParameter(PARAMETER_NAME);
        String code = request.getParameter(PARAMETER_CODE);
        String sign = request.getParameter(PARAMETER_SIGN);

        if (isCurrencyComponentsValid(name, code, sign)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log(ERROR_400);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
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

    private boolean isCurrencyComponentsValid(String name, String code, String sign) {
        return name == null || name.isEmpty() || code == null || code.isEmpty() || sign == null || sign.isEmpty();
    }
}