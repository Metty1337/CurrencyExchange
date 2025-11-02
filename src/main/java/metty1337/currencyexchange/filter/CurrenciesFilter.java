package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonManager;
import metty1337.currencyexchange.util.ValidatorManager;

import java.io.IOException;

@WebFilter("/currencies")
public class CurrenciesFilter implements Filter {
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_SIGN = "sign";
    private static final String POST_REQUEST = "POST";
    private static final String ERROR_400 = "A required field is missing";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonManager.prepareResponse(response);

        String method = request.getMethod();
        if (method.equals(POST_REQUEST)) {
            String name = request.getParameter(PARAMETER_NAME);
            String code = request.getParameter(PARAMETER_CODE);
            String sign = request.getParameter(PARAMETER_SIGN);
            if (!ValidatorManager.isCurrencyComponentsValid(name, code, sign)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonManager.writeJsonError(response, ERROR_400);
            } else {
                request.setAttribute(PARAMETER_NAME, name);
                request.setAttribute(PARAMETER_CODE, code);
                request.setAttribute(PARAMETER_SIGN, sign);
            }
        }

        filterChain.doFilter(request, response);
    }
}
