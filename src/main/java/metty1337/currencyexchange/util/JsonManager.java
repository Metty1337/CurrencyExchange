package metty1337.currencyexchange.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import metty1337.currencyexchange.errors.ErrorCell;

import java.io.IOException;

public final class JsonManager {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeJsonError(HttpServletResponse response, String message) {
        ErrorCell error = new ErrorCell(message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(response.getWriter(), error);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeJsonResult(HttpServletResponse response, Object result) {
        try {
            objectMapper.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
