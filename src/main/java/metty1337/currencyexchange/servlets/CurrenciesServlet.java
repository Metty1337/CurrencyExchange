package metty1337.currencyexchange.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.errors.ErrorCell;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.CurrencyAlreadyExists;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_SIGN = "sign";
    public static final String ERROR_400 = "A required field is missing";
    public static final String ERROR_409 = "Currency with this code already exists";

    @Override
    public void init() {
        CurrencyMapper currencyMapper = new CurrencyMapper();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        this.currencyService = new CurrencyService(currencyDAO, currencyMapper);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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
                currencyDTO = currencyService.getCurrency(currencyDTO.getCode());
                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonManager.writeJsonResult(response, currencyDTO);
            } catch (RuntimeException e) {
                if (e instanceof CurrencyAlreadyExists) {
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