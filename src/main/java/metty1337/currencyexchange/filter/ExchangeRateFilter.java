package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonManager;
import metty1337.currencyexchange.util.ValidatorManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebFilter("/exchangeRate/*")
public class ExchangeRateFilter implements Filter {
    private static final String GET_REQUEST = "GET";
    private static final String PATCH_REQUEST = "PATCH";
    private static final String PARAMETER_RATE = "rate";
    private static final String ATTRIBUTE_RATE = "rate";
    private static final String ERROR_400 = "Currency Code Is Missing at the address";
    private static final String BASE_CURRENCY_CODE_ATTRIBUTE = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_ATTRIBUTE = "targetCurrencyCode";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonManager.prepareResponse(response);

        String method = request.getMethod();
        String input = request.getPathInfo();
        if (method.equals(GET_REQUEST)) {
            if (!ValidatorManager.isCurrencyCodesRequestValid(input)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonManager.writeJsonError(response, ERROR_400);
            } else {
                getCodesAndSetThemAsAttributes(request, input);
                filterChain.doFilter(request, response);
            }
        } else if (method.equals(PATCH_REQUEST)) {
            String rateInput = request.getParameter(PARAMETER_RATE);
            if (!ValidatorManager.isCurrencyCodesRequestValid(input) || !ValidatorManager.isRateInputValid(rateInput)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonManager.writeJsonError(response, ERROR_400);
            } else {
                getCodesAndSetThemAsAttributes(request, input);
                BigDecimal rate = new BigDecimal(rateInput).setScale(6, RoundingMode.HALF_UP);
                request.setAttribute(ATTRIBUTE_RATE, rate);
                filterChain.doFilter(request, response);
            }
        }
    }

    private void getCodesAndSetThemAsAttributes(HttpServletRequest request, String input) {
        String baseCurrencyCode = getBaseCurrencyCode(input);
        String targetCurrencyCode = getTargetCurrencyCode(input);
        request.setAttribute(BASE_CURRENCY_CODE_ATTRIBUTE, baseCurrencyCode);
        request.setAttribute(TARGET_CURRENCY_CODE_ATTRIBUTE, targetCurrencyCode);
    }

    private String getBaseCurrencyCode(String input) {
        return input.substring(1, 4);
    }

    private String getTargetCurrencyCode(String input) {
        return input.substring(4);
    }
}
