package metty1337.currencyexchange.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dao.ExchangeRateDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.dto.ExchangeRateDTO;
import metty1337.currencyexchange.errors.ErrorMessages;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.service.CurrencyService;
import metty1337.currencyexchange.service.ExchangeRateService;
import metty1337.currencyexchange.util.JsonManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ExchangeRates", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        this.exchangeRateService = createExchangeRateService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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

    }

    private ExchangeRateService createExchangeRateService() {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        CurrencyMapper currencyMapper = new CurrencyMapper();
        CurrencyService currencyService = new CurrencyService(currencyDAO, currencyMapper);
        return new ExchangeRateService(exchangeRateDAO, currencyService);
    }
}