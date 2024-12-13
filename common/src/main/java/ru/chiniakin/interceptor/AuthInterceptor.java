package ru.chiniakin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.AuthorizationException;
import ru.chiniakin.http.HttpAuthService;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.util.RequestAttributeUtil;

/**
 * Интерсептор для обработки аутентификации запросов.
 *
 * @author ChiniakinD
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private String BEARER_PREFIX = "Bearer ";
    private final UserRepository userRepository;
    private final HttpAuthService authService;

    /**
     * Метод, выполняемый перед обработкой запроса.
     *
     * @param request  запрос.
     * @param response ответ.
     * @param handler  обработчик.
     * @return true, если обработка запроса продолжается.
     * @throws Exception в случае ошибки при аутентификации.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String jwt = extractJwtFromRequest(request);
        if (jwt == null || jwt.isEmpty()) {
            throw new AccessDeniedException("Невалидный токен");
        }
        try {
            String login = authService.auth(jwt);
            User user = userRepository.findUserByLoginOrThrow(login);
            RequestAttributeUtil.set(request, AttributeName.LOGIN, user);
        } catch (RuntimeException e) {
            log.error("Auth error: {}", e.getMessage());
            throw new AuthorizationException(e.getMessage());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * Метод, выполняемый после обработки запроса.
     *
     * @param request  запрос.
     * @param response ответ.
     * @param handler  обработчик.
     * @throws Exception в случае ошибки.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * Извлекает JWT токен из заголовка запроса.
     *
     * @param request запрос
     * @return JWT токен или null, если токен отсутствует
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;
    }

}
