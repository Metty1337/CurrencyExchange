package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonManager;
import metty1337.currencyexchange.util.ValidatorManager;

import java.io.IOException;
import java.math.BigDecimal;

@WebFilter("/exchange")
public class ExchangeFilter implements Filter {
    private static final String BASE_CURRENCY_CODE_PARAMETER = "from";
    private static final String TARGET_CURRENCY_CODE_PARAMETER = "to";
    private static final String AMOUNT_PARAMETER = "amount";
    private static final String ERROR_400 = "A required field is missing";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonManager.prepareResponse(response);

        String baseCurrencyCode = request.getParameter(BASE_CURRENCY_CODE_PARAMETER);
        String targetCurrencyCode = request.getParameter(TARGET_CURRENCY_CODE_PARAMETER);
        String amount = request.getParameter(AMOUNT_PARAMETER);

        if (!ValidatorManager.isExchangeInputValid(baseCurrencyCode, targetCurrencyCode, amount)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonManager.writeJsonError(response, ERROR_400);
        } else {
            request.setAttribute(BASE_CURRENCY_CODE_PARAMETER, baseCurrencyCode);
            request.setAttribute(TARGET_CURRENCY_CODE_PARAMETER, targetCurrencyCode);
            request.setAttribute(AMOUNT_PARAMETER, amount);
            filterChain.doFilter(request, response);
        }
    }
}
