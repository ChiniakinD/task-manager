package ru.chiniakin.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.chiniakin.exception.AccessDeniedException;

import java.io.IOException;

/**
 * Обработчик исключений для неавторизованных пользователей.
 *
 * @author ChiniakinD
 */
@Component
@RequiredArgsConstructor
public class SecurityFilterException implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param request       объект запроса.
     * @param response      объект ответа.
     * @param authException исключение, связанное с аутентификацией.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ApiError apiError = new ApiError()
                .setStatus(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .setMessage("Пользователь не авторизован")
                .setDetails(authException.getMessage());
        setResponse(response, apiError);
    }

    /**
     * Устанавливает ответ с кодом статуса 401 данными, содержащими информацию об ошибке.
     *
     * @param response объект ответа.
     * @param apiError модель с данными об ошибке.
     */
    private void setResponse(HttpServletResponse response, ApiError apiError) {
        try {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiError));
        } catch (IOException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
}
