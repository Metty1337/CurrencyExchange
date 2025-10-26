package metty1337.currencyexchange.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.dao.CurrencyDAO;
import metty1337.currencyexchange.dto.CurrencyDTO;
import metty1337.currencyexchange.errors.Error500;
import metty1337.currencyexchange.mapper.CurrencyMapper;
import metty1337.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        CurrencyMapper currencyMapper = new CurrencyMapper();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        this.currencyService = new CurrencyService(currencyDAO, currencyMapper);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<CurrencyDTO> currenciesDTO = currencyService.getCurrencies();

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), currenciesDTO);
        } catch (RuntimeException e) {
            Error500 error500 = new Error500();
            log(error500.message, e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), error500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}