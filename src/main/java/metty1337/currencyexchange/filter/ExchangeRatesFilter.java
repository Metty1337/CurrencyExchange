package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonResponseWriter;
import metty1337.currencyexchange.util.Validator;

import java.io.IOException;

@WebFilter("/exchangeRates")
public class ExchangeRatesFilter implements Filter {
    private static final String POST_REQUEST = "POST";
    private static final String BASE_CURRENCY_CODE_PARAMETER = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_PARAMETER = "targetCurrencyCode";
    private static final String RATE_PARAMETER = "rate";
    private static final String ERROR_400 = "A required field is missing";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonResponseWriter.prepareResponse(response);

        String method = request.getMethod();

        if (method.equals(POST_REQUEST)) {
            String baseCurrencyCode = request.getParameter(BASE_CURRENCY_CODE_PARAMETER);
            String targetCurrencyCode = request.getParameter(TARGET_CURRENCY_CODE_PARAMETER);
            String rate = request.getParameter(RATE_PARAMETER);
            if (!Validator.isExchangeRateInputValid(baseCurrencyCode, targetCurrencyCode, rate)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonResponseWriter.writeJsonError(response, ERROR_400);
            } else {
                request.setAttribute(BASE_CURRENCY_CODE_PARAMETER, baseCurrencyCode);
                request.setAttribute(TARGET_CURRENCY_CODE_PARAMETER, targetCurrencyCode);
                request.setAttribute(RATE_PARAMETER, rate);
            }
        }
        filterChain.doFilter(request, response);
    }
}
