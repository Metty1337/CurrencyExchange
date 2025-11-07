package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonResponseWriter;
import metty1337.currencyexchange.util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebFilter("/exchangeRate/*")
public class ExchangeRateFilter implements Filter {
    private static final String GET_REQUEST = "GET";
    private static final String PATCH_REQUEST = "PATCH";
    private static final String RATE_PARAMETER = "rate";
    private static final String ERROR_400 = "Currency Code Is Missing at the address";
    private static final String BASE_CURRENCY_CODE_ATTRIBUTE = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_ATTRIBUTE = "targetCurrencyCode";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonResponseWriter.prepareResponse(response);

        String method = request.getMethod();
        String input = request.getPathInfo();
        if (method.equals(GET_REQUEST)) {
            if (!Validator.isCurrencyCodesRequestValid(input)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonResponseWriter.writeJsonError(response, ERROR_400);
            } else {
                getCodesAndSetThemAsAttributes(request, input);
                filterChain.doFilter(request, response);
            }
        }
        else if (method.equals(PATCH_REQUEST))
        {
            Map<String, String> formData = parseFormData(request);
            String rateInput = formData.get(RATE_PARAMETER);
//            String rateInput = request.getParameter(RATE_PARAMETER);
            if (!Validator.isCurrencyCodesRequestValid(input) || !Validator.isRateInputValid(rateInput)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonResponseWriter.writeJsonError(response, ERROR_400);
            } else {
                getCodesAndSetThemAsAttributes(request, input);
                BigDecimal rate = new BigDecimal(rateInput).setScale(6, RoundingMode.HALF_UP);
                request.setAttribute(RATE_PARAMETER, rate);
                filterChain.doFilter(request, response);
            }
        }
    }

    private Map<String, String> parseFormData(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        Map<String, String> formData = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                formData.put(key, value);
            }
        }
        return formData;
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
