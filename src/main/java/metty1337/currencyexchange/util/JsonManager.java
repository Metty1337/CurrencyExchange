package metty1337.currencyexchange.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.errors.ErrorCell;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonManager {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String UTF_8_CHARACTER_ENCODING = "UTF-8";
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
            OBJECT_MAPPER.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void prepareResponse(HttpServletResponse response) {
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8_CHARACTER_ENCODING);
    }
}
