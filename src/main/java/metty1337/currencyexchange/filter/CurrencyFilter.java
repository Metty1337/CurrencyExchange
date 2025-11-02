package metty1337.currencyexchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.util.JsonManager;
import metty1337.currencyexchange.util.ValidatorManager;

import java.io.IOException;

@WebFilter("/currency/*")
public class CurrencyFilter implements Filter {
    private static final String CODE_ATTRIBUTE = "code";
    private static final String ERROR_400_MESSAGE = "Currency Code Is Missing at the address";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JsonManager.prepareResponse(response);

        String code = request.getPathInfo();
        if (!ValidatorManager.isCurrencyCodeRequestValid(code)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonManager.writeJsonError(response, ERROR_400_MESSAGE);
        } else {
            code = transformInRightFormat(code);
            request.setAttribute(CODE_ATTRIBUTE, code);
            filterChain.doFilter(request, response);
        }
    }

    private String transformInRightFormat(String code) {
        return code.substring(1);
    }
}
